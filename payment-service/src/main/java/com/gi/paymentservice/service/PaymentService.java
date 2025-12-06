package com.gi.paymentservice.service;

import com.gi.paymentservice.model.dto.request.PaymentRequestDTO;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;

import java.util.List;

public interface PaymentService {

    SubscriptionPaymentDTO initiatePayment(PaymentRequestDTO request);

    void processStripeWebhook(String payload, String signature);

    SubscriptionPaymentDTO findById(Long id);

    List<SubscriptionPaymentDTO> listPaymentsByCabinet(Long cabinetId);
}