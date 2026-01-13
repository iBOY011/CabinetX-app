package com.gi.paymentservice.client;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gi.paymentservice.config.StripeProperties;
import com.gi.paymentservice.model.entity.SubscriptionPayment;
import com.gi.paymentservice.model.enums.PaymentStatus;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.InvoiceCollection;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.InvoiceListParams;
import com.stripe.param.SubscriptionCancelParams;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeClient {

    private final StripeProperties properties;

    @PostConstruct
    void initStripeApi() {
        Stripe.apiKey = properties.getSecretKey();
    }

    public Customer createCustomer(String email, String name, Long cabinetId) throws StripeException {
        CustomerCreateParams.Builder builder = CustomerCreateParams.builder();
        if (StringUtils.hasText(email)) {
            builder.setEmail(email);
        }
        if (StringUtils.hasText(name)) {
            builder.setName(name);
        }
        if (cabinetId != null) {
            builder.putMetadata("cabinetId", String.valueOf(cabinetId));
        }
        return Customer.create(builder.build());
    }

    public Customer retrieveCustomer(String stripeCustomerId) throws StripeException {
        return Customer.retrieve(stripeCustomerId);
    }

    /**
     * Simplified checkout placeholder: generate a mock session id without creating a Stripe Checkout session.
     */
    public String createCheckoutSessionId(String clientReference) {
        String suffix = StringUtils.hasText(clientReference) ? clientReference : String.valueOf(System.currentTimeMillis());
        return "cs_mock_" + suffix;
    }

    public Event parseEvent(String payload, String signature) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, signature, properties.getWebhookSecret());
    }

    public Subscription retrieveSubscription(String subscriptionId) throws StripeException {
        return Subscription.retrieve(subscriptionId);
    }

    public Subscription cancelSubscription(String subscriptionId) throws StripeException {
        Subscription subscription = Subscription.retrieve(subscriptionId);
        SubscriptionCancelParams params = SubscriptionCancelParams.builder()
            .setInvoiceNow(false)
            .setProrate(false)
            .build();
        return subscription.cancel(params);
    }

    public InvoiceCollection listInvoicesForCustomer(String customerId, Long limit) throws StripeException {
        InvoiceListParams params = InvoiceListParams.builder()
            .setCustomer(customerId)
            .setLimit(Optional.ofNullable(limit).orElse(20L))
            .build();
        return com.stripe.model.Invoice.list(params);
    }

    /**
     * Legacy helpers kept for compatibility with PaymentServiceImpl.
     */
    public String createCheckoutSession(SubscriptionPayment payment) {
        return "cs_mock_" + payment.getId();
    }

    public PaymentStatus verifyPayment(String sessionId) {
        return PaymentStatus.SUCCESSFUL;
    }

}