package com.gi.paymentservice.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/checkout-session")
    public ResponseEntity<CheckoutSessionResponse> createCheckoutSession(@RequestBody CheckoutSessionRequest request) {
        return ResponseEntity.ok(billingService.createCheckoutSession(request));
    }

    @PostMapping("/customer")
    public ResponseEntity<BillingCustomerDTO> createCustomer(@RequestBody BillingCustomerRequest request) {
        return ResponseEntity.ok(billingService.getOrCreateCustomer(request));
    }

    @GetMapping("/customer")
    public ResponseEntity<BillingCustomerDTO> getCustomer(@RequestParam Long cabinetId) {
        return ResponseEntity.ok(billingService.getOrCreateCustomer(new BillingCustomerRequest(cabinetId, null, null)));
    }

    @GetMapping("/plans")
    public ResponseEntity<List<BillingPlanDTO>> listPlans() {
        return ResponseEntity.ok(billingService.listPlans());
    }

    @GetMapping("/subscription")
    public ResponseEntity<BillingSubscriptionDTO> getSubscription(@RequestParam Long cabinetId) {
        return ResponseEntity.ok(billingService.getSubscription(cabinetId));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<SubscriptionPaymentDTO>> listInvoices(@RequestParam Long cabinetId) {
        return ResponseEntity.ok(billingService.getInvoices(cabinetId));
    }

    @PostMapping("/subscription/cancel")
    public ResponseEntity<BillingSubscriptionDTO> cancelSubscription(@RequestBody CancelSubscriptionRequest request) {
        return ResponseEntity.ok(billingService.cancelSubscription(request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader("Stripe-Signature") String signature) {
        billingService.handleStripeWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
