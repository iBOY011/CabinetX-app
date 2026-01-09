package com.gi.paymentservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gi.paymentservice.model.dto.request.BillingCustomerRequest;
import com.gi.paymentservice.model.dto.request.CancelSubscriptionRequest;
import com.gi.paymentservice.model.dto.request.CheckoutSessionRequest;
import com.gi.paymentservice.model.dto.response.BillingCustomerDTO;
import com.gi.paymentservice.model.dto.response.BillingPlanDTO;
import com.gi.paymentservice.model.dto.response.BillingSubscriptionDTO;
import com.gi.paymentservice.model.dto.response.CheckoutSessionResponse;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;
import com.gi.paymentservice.service.BillingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/checkout-session")
    public ResponseEntity<CheckoutSessionResponse> createCheckoutSession(@RequestBody CheckoutSessionRequest request) {
        log.info("[Billing] POST /checkout-session body={} ", request);
        CheckoutSessionResponse response = billingService.createCheckoutSession(request);
        log.info("[Billing] POST /checkout-session response={} ", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer")
    public ResponseEntity<BillingCustomerDTO> createCustomer(@RequestBody BillingCustomerRequest request) {
        log.info("[Billing] POST /customer body={} ", request);
        BillingCustomerDTO response = billingService.getOrCreateCustomer(request);
        log.info("[Billing] POST /customer response={} ", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer")
    public ResponseEntity<BillingCustomerDTO> getCustomer(@RequestParam Long cabinetId) {
        log.info("[Billing] GET /customer cabinetId={} ", cabinetId);
        BillingCustomerDTO response = billingService.getOrCreateCustomer(new BillingCustomerRequest(cabinetId, null, null));
        log.info("[Billing] GET /customer cabinetId={} response={} ", cabinetId, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans")
    public ResponseEntity<List<BillingPlanDTO>> listPlans() {
        log.info("[Billing] GET /plans");
        List<BillingPlanDTO> response = billingService.listPlans();
        log.info("[Billing] GET /plans response size={}", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscription")
    public ResponseEntity<BillingSubscriptionDTO> getSubscription(@RequestParam Long cabinetId) {
        log.info("[Billing] GET /subscription cabinetId={} ", cabinetId);
        BillingSubscriptionDTO response = billingService.getSubscription(cabinetId);
        log.info("[Billing] GET /subscription cabinetId={} response={} ", cabinetId, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<SubscriptionPaymentDTO>> listInvoices(@RequestParam Long cabinetId) {
        log.info("[Billing] GET /invoices cabinetId={}", cabinetId);
        List<SubscriptionPaymentDTO> response = billingService.getInvoices(cabinetId);
        log.info("[Billing] GET /invoices cabinetId={} response size={}", cabinetId, response.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/cancel")
    public ResponseEntity<BillingSubscriptionDTO> cancelSubscription(@RequestBody CancelSubscriptionRequest request) {
        log.info("[Billing] POST /subscription/cancel body={} ", request);
        BillingSubscriptionDTO response = billingService.cancelSubscription(request);
        log.info("[Billing] POST /subscription/cancel response={} ", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/subscription/purge")
    public ResponseEntity<Void> purgeSubscription(@RequestParam Long cabinetId) {
        log.info("[Billing] DELETE /subscription/purge cabinetId={}", cabinetId);
        billingService.purgeSubscription(cabinetId);
        log.info("[Billing] DELETE /subscription/purge cabinetId={} done", cabinetId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader("Stripe-Signature") String signature) {
        log.info("[Billing] POST /webhook signature={} payloadLength={}", signature, payload != null ? payload.length() : 0);
        billingService.handleStripeWebhook(payload, signature);
        log.info("[Billing] POST /webhook processed");
        return ResponseEntity.ok().build();
    }
}
