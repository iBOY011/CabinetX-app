package com.gi.paymentservice.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.gi.paymentservice.client.StripeClient;
import com.gi.paymentservice.config.StripeProperties;
import com.gi.paymentservice.exception.ResourceNotFoundException;
import com.gi.paymentservice.mapper.SubscriptionPaymentMapper;
import com.gi.paymentservice.model.dto.request.BillingCustomerRequest;
import com.gi.paymentservice.model.dto.request.CancelSubscriptionRequest;
import com.gi.paymentservice.model.dto.request.CheckoutSessionRequest;
import com.gi.paymentservice.model.dto.response.BillingCustomerDTO;
import com.gi.paymentservice.model.dto.response.BillingPlanDTO;
import com.gi.paymentservice.model.dto.response.BillingSubscriptionDTO;
import com.gi.paymentservice.model.dto.response.CheckoutSessionResponse;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;
import com.gi.paymentservice.model.entity.BillingCustomer;
import com.gi.paymentservice.model.entity.BillingSubscription;
import com.gi.paymentservice.model.entity.SubscriptionPayment;
import com.gi.paymentservice.model.enums.PaymentStatus;
import com.gi.paymentservice.model.enums.PaymentType;
import com.gi.paymentservice.model.enums.SubscriptionStatus;
import com.gi.paymentservice.repository.BillingCustomerRepository;
import com.gi.paymentservice.repository.BillingSubscriptionRepository;
import com.gi.paymentservice.repository.SubscriptionPaymentRepository;
import com.gi.paymentservice.service.BillingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import com.stripe.model.Subscription;
import com.stripe.net.ApiResource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {

    private final StripeClient stripeClient;
    private final StripeProperties stripeProperties;
    private final BillingCustomerRepository customerRepository;
    private final BillingSubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository paymentRepository;
    private final SubscriptionPaymentMapper paymentMapper;

    @Override
    @Transactional
    public CheckoutSessionResponse createCheckoutSession(CheckoutSessionRequest request) {
        if (!StringUtils.hasText(request.getPriceId())) {
            request.setPriceId(resolvePriceId(request.getPlanId()));
        }
        if (!StringUtils.hasText(request.getPriceId())) {
            throw new IllegalArgumentException("priceId or planId is required");
        }

        BillingCustomer customer = ensureCustomer(new BillingCustomerRequest(
            request.getCabinetId(),
            request.getCustomerEmail(),
            request.getCustomerName()
        ));

        BillingSubscription subscription = subscriptionRepository.findByCabinetId(request.getCabinetId())
            .orElseGet(() -> BillingSubscription.builder()
                .cabinetId(request.getCabinetId())
                .createdAt(LocalDateTime.now())
                .build());

        subscription.setStripeCustomerId(customer.getStripeCustomerId());
        subscription.setPriceId(request.getPriceId());
        subscription.setPlanName(resolvePlanName(request.getPlanId(), request.getPriceId()));
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setAutoRenew(true);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscription = subscriptionRepository.save(subscription);

        try {
            com.stripe.model.checkout.Session session = stripeClient.createCheckoutSession(
                customer.getStripeCustomerId(),
                request.getPriceId(),
                request.getCabinetId(),
                request.getSuccessUrl(),
                request.getCancelUrl()
            );
            subscription.setCheckoutSessionId(session.getId());
            subscriptionRepository.save(subscription);
            log.info("[Billing] Created Stripe checkout session {} for cabinet {}", session.getId(), request.getCabinetId());
            return new CheckoutSessionResponse(session.getId(), session.getUrl());
        } catch (Exception e) {
            log.error("[Billing] Failed to create Stripe checkout session for cabinet {}: {}", request.getCabinetId(), e.getMessage());
            throw new RuntimeException("Failed to create checkout session", e);
        }
    }

    @Override
    @Transactional
    public BillingCustomerDTO getOrCreateCustomer(BillingCustomerRequest request) {
        BillingCustomer customer = ensureCustomer(request);
        return new BillingCustomerDTO(
            customer.getCabinetId(),
            customer.getStripeCustomerId(),
            customer.getEmail(),
            customer.getName(),
            customer.getCreatedAt()
        );
    }

    @Override
    public List<BillingPlanDTO> listPlans() {
        List<BillingPlanDTO> plans = new ArrayList<>();
        for (Map.Entry<String, StripeProperties.Plan> entry : stripeProperties.getPlans().entrySet()) {
            StripeProperties.Plan plan = entry.getValue();
            plans.add(new BillingPlanDTO(
                entry.getKey(),
                plan.getPriceId(),
                plan.getName(),
                plan.getAmount(),
                plan.getCurrency(),
                plan.getInterval(),
                plan.getDescription()
            ));
        }
        plans.sort(Comparator.comparing(BillingPlanDTO::getAmount, Comparator.nullsLast(Long::compareTo)));
        return plans;
    }

    @Override
    @Transactional(readOnly = true)
    public BillingSubscriptionDTO getSubscription(Long cabinetId) {
        BillingSubscription subscription = subscriptionRepository.findByCabinetId(cabinetId)
            .orElseThrow(() -> new ResourceNotFoundException("No subscription found for cabinet " + cabinetId));

        if (StringUtils.hasText(subscription.getStripeSubscriptionId())) {
            try {
                Subscription stripeSubscription = stripeClient.retrieveSubscription(subscription.getStripeSubscriptionId());
                subscription = updateSubscriptionFromStripe(subscription, stripeSubscription);
            } catch (StripeException e) {
                log.warn("Unable to refresh subscription {} from Stripe", subscription.getStripeSubscriptionId(), e);
            }
        }

        return new BillingSubscriptionDTO(
            subscription.getCabinetId(),
            subscription.getStripeSubscriptionId(),
            subscription.getPriceId(),
            subscription.getPlanName(),
            subscription.getStatus(),
            subscription.getAutoRenew(),
            subscription.getCurrentPeriodStart(),
            subscription.getCurrentPeriodEnd()
        );
    }

    @Override
    @Transactional
    public List<SubscriptionPaymentDTO> getInvoices(Long cabinetId) {
        log.info("[Billing] getInvoices for cabinet {}", cabinetId);
        Optional<BillingCustomer> customerOpt = customerRepository.findByCabinetId(cabinetId);
        
        if (customerOpt.isEmpty()) {
            log.info("[Billing] No Stripe customer yet for cabinet {}, returning empty invoices list", cabinetId);
            return List.of();
        }
        
        BillingCustomer customer = customerOpt.get();
        log.info("[Billing] Found customer {} for cabinet {}", customer.getStripeCustomerId(), cabinetId);
        
        try {
            log.info("[Billing] Fetching invoices from Stripe for customer {}", customer.getStripeCustomerId());
            InvoiceCollection invoices = stripeClient.listInvoicesForCustomer(customer.getStripeCustomerId(), 25L);
            log.info("[Billing] Received {} invoices from Stripe", invoices.getData().size());
            invoices.getData().forEach(invoice -> {
                try {
                    upsertInvoice(invoice, cabinetId);
                } catch (Exception e) {
                    log.error("[Billing] Failed to upsert invoice {} for cabinet {}: {}", invoice.getId(), cabinetId, e.getMessage(), e);
                }
            });
        } catch (StripeException e) {
            log.error("[Billing] Stripe API error for cabinet {}: {}", cabinetId, e.getMessage(), e);
        } catch (Exception e) {
            log.error("[Billing] Unexpected error syncing invoices for cabinet {}: {}", cabinetId, e.getMessage(), e);
        }

        List<SubscriptionPaymentDTO> payments = paymentRepository.findByCabinetIdOrderByCreatedAtDesc(cabinetId).stream()
            .map(paymentMapper::toDTO)
            .toList();
        log.info("[Billing] Returning {} payments for cabinet {}", payments.size(), cabinetId);
        return payments;
    }

    @Override
    @Transactional
    public BillingSubscriptionDTO cancelSubscription(CancelSubscriptionRequest request) {
        BillingSubscription subscription = subscriptionRepository.findByCabinetId(request.getCabinetId())
            .orElseThrow(() -> new ResourceNotFoundException("No subscription found for cabinet " + request.getCabinetId()));
        if (!StringUtils.hasText(subscription.getStripeSubscriptionId())) {
            throw new IllegalStateException("Subscription not active yet");
        }
        try {
            Subscription stripeSubscription = stripeClient.cancelSubscription(subscription.getStripeSubscriptionId());
            subscription = updateSubscriptionFromStripe(subscription, stripeSubscription);
        } catch (StripeException e) {
            log.error("Stripe cancellation failed for subscription {}", subscription.getStripeSubscriptionId(), e);
            throw new IllegalStateException("Stripe cancellation failed: " + e.getMessage());
        }

        return new BillingSubscriptionDTO(
            subscription.getCabinetId(),
            subscription.getStripeSubscriptionId(),
            subscription.getPriceId(),
            subscription.getPlanName(),
            subscription.getStatus(),
            subscription.getAutoRenew(),
            subscription.getCurrentPeriodStart(),
            subscription.getCurrentPeriodEnd()
        );
    }

    @Override
    @Transactional
    public void handleStripeWebhook(String payload, String signature) {
        Event event;
        try {
            event = stripeClient.parseEvent(payload, signature);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature", e);
            throw new IllegalArgumentException("Invalid Stripe webhook signature");
        }

        switch (event.getType()) {
            case "invoice.paid", "invoice.payment_failed" -> handleInvoiceEvent(event);
            case "customer.subscription.updated", "customer.subscription.deleted" -> handleSubscriptionEvent(event);
            default -> log.debug("Unhandled Stripe event: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void purgeSubscription(Long cabinetId) {
        paymentRepository.deleteAll(paymentRepository.findByCabinetId(cabinetId));
        subscriptionRepository.findByCabinetId(cabinetId)
            .ifPresent(subscriptionRepository::delete);
    }

    private void handleInvoiceEvent(Event event) {
        Invoice invoice = deserialize(event, Invoice.class);
        if (invoice == null) {
            return;
        }
        Long cabinetId = resolveCabinetIdFromInvoice(invoice);
        if (cabinetId == null) {
            log.warn("Cannot resolve cabinet for invoice {}", invoice.getId());
            return;
        }
        upsertInvoice(invoice, cabinetId);
    }

    private void handleSubscriptionEvent(Event event) {
        Subscription subscription = deserialize(event, Subscription.class);
        if (subscription == null) {
            return;
        }
        BillingSubscription entity = subscriptionRepository.findByStripeSubscriptionId(subscription.getId())
            .orElseGet(() -> BillingSubscription.builder()
                .stripeSubscriptionId(subscription.getId())
                .stripeCustomerId(subscription.getCustomer())
                .cabinetId(resolveCabinetIdFromMetadata(subscription.getMetadata()))
                .createdAt(LocalDateTime.now())
                .build());

        updateSubscriptionFromStripe(entity, subscription);
    }

    private BillingSubscription updateSubscriptionFromStripe(BillingSubscription entity, Subscription stripeSubscription) {
        entity.setStripeSubscriptionId(stripeSubscription.getId());
        entity.setStripeCustomerId(stripeSubscription.getCustomer());
        String stripePlanName = stripeSubscription.getItems().getData().isEmpty() ? null :
            stripeSubscription.getItems().getData().getFirst().getPrice().getNickname();
        String stripePriceId = stripeSubscription.getItems().getData().isEmpty() ? null :
            stripeSubscription.getItems().getData().getFirst().getPrice().getId();

        entity.setPriceId(stripePriceId != null ? stripePriceId : entity.getPriceId());
        entity.setPlanName(StringUtils.hasText(stripePlanName)
            ? stripePlanName
            : resolvePlanNameFromPriceId(entity.getPriceId()));
        entity.setStatus(mapSubscriptionStatus(stripeSubscription.getStatus()));
        entity.setCurrentPeriodStart(epochToLocalDateTime(stripeSubscription.getCurrentPeriodStart()));
        entity.setCurrentPeriodEnd(epochToLocalDateTime(stripeSubscription.getCurrentPeriodEnd()));
        entity.setAutoRenew(!Boolean.TRUE.equals(stripeSubscription.getCancelAtPeriodEnd()));
        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getCabinetId() == null) {
            entity.setCabinetId(resolveCabinetIdFromMetadata(stripeSubscription.getMetadata()));
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        return subscriptionRepository.save(entity);
    }

    private void upsertInvoice(Invoice invoice, Long cabinetId) {
        BillingSubscription subscription = subscriptionRepository.findByStripeSubscriptionId(invoice.getSubscription())
            .orElseGet(() -> subscriptionRepository.findByCabinetId(cabinetId)
                .orElseGet(() -> BillingSubscription.builder()
                    .cabinetId(cabinetId)
                    .stripeCustomerId(invoice.getCustomer())
                    .stripeSubscriptionId(invoice.getSubscription())
                    .status(SubscriptionStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()));
        
        if (subscription.getStripeSubscriptionId() == null) {
            subscription.setStripeSubscriptionId(invoice.getSubscription());
        }
        if (subscription.getStripeCustomerId() == null) {
            subscription.setStripeCustomerId(invoice.getCustomer());
        }
        subscriptionRepository.save(subscription);

        SubscriptionPayment payment = paymentRepository.findByStripeInvoiceId(invoice.getId())
            .orElseGet(() -> {
                SubscriptionPayment p = new SubscriptionPayment();
                p.setCreatedAt(epochToLocalDateTime(invoice.getCreated()));
                return p;
            });

        payment.setSubscriptionId(subscription.getId());
        payment.setCabinetId(cabinetId);
        payment.setAmount(BigDecimal.valueOf(Optional.ofNullable(invoice.getAmountPaid()).orElse(invoice.getAmountDue()) / 100.0));
        payment.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency().toUpperCase() : "EUR");
        payment.setStripeInvoiceId(invoice.getId());
        payment.setInvoiceUrl(invoice.getHostedInvoiceUrl());
        payment.setPeriodStart(epochToLocalDate(invoice.getPeriodStart()));
        payment.setPeriodEnd(epochToLocalDate(invoice.getPeriodEnd()));
        if (payment.getDoctorId() == null) {
            payment.setDoctorId(subscription.getCabinetId());
        }
        if (payment.getPaymentType() == null) {
            payment.setPaymentType(PaymentType.SUBSCRIPTION_CABINET);
        }
        payment.setStatus(mapInvoiceStatus(invoice.getStatus()));
        if (payment.getStatus() == PaymentStatus.SUCCESSFUL) {
            payment.setPaidAt(LocalDateTime.now());
        }
        paymentRepository.save(payment);
    }

    private BillingCustomer ensureCustomer(BillingCustomerRequest request) {
        return customerRepository.findByCabinetId(request.getCabinetId())
            .orElseGet(() -> {
                try {
                    var customer = stripeClient.createCustomer(request.getEmail(), request.getName(), request.getCabinetId());
                    LocalDateTime now = LocalDateTime.now();
                    BillingCustomer entity = BillingCustomer.builder()
                        .cabinetId(request.getCabinetId())
                        .stripeCustomerId(customer.getId())
                        .email(request.getEmail())
                        .name(request.getName())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                    return customerRepository.save(entity);
                } catch (StripeException e) {
                    log.error("Failed to create Stripe customer for cabinet {}", request.getCabinetId(), e);
                    throw new IllegalStateException("Stripe customer creation failed: " + e.getMessage());
                }
            });
    }

    private PaymentStatus mapInvoiceStatus(String status) {
        return switch (status == null ? "" : status) {
            case "paid" -> PaymentStatus.SUCCESSFUL;
            case "open", "draft", "uncollectible" -> PaymentStatus.PENDING;
            case "void", "canceled", "unpaid" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.FAILED;
        };
    }

    private SubscriptionStatus mapSubscriptionStatus(String status) {
        return switch (status == null ? "" : status) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            case "incomplete_expired" -> SubscriptionStatus.INCOMPLETE_EXPIRED;
            case "past_due" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "unpaid" -> SubscriptionStatus.UNPAID;
            default -> SubscriptionStatus.PENDING;
        };
    }

    private LocalDate epochToLocalDate(Long epoch) {
        if (epoch == null) {
            return null;
        }
        return Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private LocalDateTime epochToLocalDateTime(Long epoch) {
        if (epoch == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneOffset.UTC);
    }

    private <T> T deserialize(Event event, Class<T> clazz) {
        try {
            return event.getDataObjectDeserializer().getObject()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .orElseGet(() -> ApiResource.GSON.fromJson(event.getDataObjectDeserializer().getRawJson(), clazz));
        } catch (Exception e) {
            log.warn("Failed to deserialize event {} to {}", event.getId(), clazz.getSimpleName(), e);
            return null;
        }
    }

    private Long resolveCabinetIdFromInvoice(Invoice invoice) {
        if (invoice.getMetadata() != null && invoice.getMetadata().get("cabinetId") != null) {
            return Long.parseLong(invoice.getMetadata().get("cabinetId"));
        }
        return customerRepository.findByStripeCustomerId(invoice.getCustomer())
            .map(BillingCustomer::getCabinetId)
            .orElse(null);
    }

    private Long resolveCabinetIdFromMetadata(Map<String, String> metadata) {
        if (metadata == null) {
            return null;
        }
        String raw = metadata.get("cabinetId");
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String resolvePriceId(String planId) {
        if (!StringUtils.hasText(planId)) {
            return null;
        }
        StripeProperties.Plan plan = stripeProperties.getPlans().get(planId);
        return plan != null ? plan.getPriceId() : null;
    }

    private String resolvePlanNameFromPriceId(String priceId) {
        if (!StringUtils.hasText(priceId)) {
            return null;
        }
        return stripeProperties.getPlans().values().stream()
            .filter(plan -> priceId.equals(plan.getPriceId()))
            .map(StripeProperties.Plan::getName)
            .findFirst()
            .orElse(null);
    }

    private String resolvePlanName(String planId, String priceId) {
        if (StringUtils.hasText(planId)) {
            StripeProperties.Plan plan = stripeProperties.getPlans().get(planId);
            if (plan != null && StringUtils.hasText(plan.getName())) {
                return plan.getName();
            }
        }
        return priceId;
    }
}
