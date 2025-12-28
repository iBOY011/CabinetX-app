package com.gi.paymentservice.controller;

import com.gi.paymentservice.model.dto.request.PaymentRequestDTO;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;
import com.gi.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<SubscriptionPaymentDTO> initiatePayment(@RequestBody PaymentRequestDTO request) {
        SubscriptionPaymentDTO response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> processStripeWebhook(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String signature) {
        paymentService.processStripeWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPaymentDTO> findById(@PathVariable Long id) {
        SubscriptionPaymentDTO response = paymentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cabinet/{cabinetId}")
    public ResponseEntity<List<SubscriptionPaymentDTO>> listPaymentsByCabinet(@PathVariable Long cabinetId) {
        List<SubscriptionPaymentDTO> response = paymentService.listPaymentsByCabinet(cabinetId);
        return ResponseEntity.ok(response);
    }
}