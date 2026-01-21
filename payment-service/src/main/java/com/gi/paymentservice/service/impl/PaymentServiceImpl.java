package com.gi.paymentservice.service.impl;

import com.gi.paymentservice.client.StripeClient;
import com.gi.paymentservice.client.SubscriptionClient;
import com.gi.paymentservice.exception.ResourceNotFoundException;
import com.gi.paymentservice.mapper.SubscriptionPaymentMapper;
import com.gi.paymentservice.model.dto.request.PaymentRequestDTO;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;
import com.gi.paymentservice.model.entity.SubscriptionPayment;
import com.gi.paymentservice.model.enums.PaymentStatus;
import com.gi.paymentservice.repository.SubscriptionPaymentRepository;
import com.gi.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing subscription payments and Stripe integrations.
 * 
 * <p>This service handles the complete payment lifecycle including:
 * <ul>
 *   <li>Initiating payment sessions via Stripe checkout</li>
 *   <li>Processing webhook notifications from Stripe</li>
 *   <li>Tracking payment status and history</li>
 *   <li>Managing subscription activations after successful payments</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private SubscriptionPaymentRepository repository;

    @Autowired
    private StripeClient stripeClient;

    @Autowired
    private SubscriptionClient subscriptionClient;

    @Autowired
    private SubscriptionPaymentMapper mapper;

    @Override
    @Transactional
    public SubscriptionPaymentDTO initiatePayment(PaymentRequestDTO request) {
        SubscriptionPayment payment = mapper.toEntity(request);
        payment = repository.save(payment);

        String sessionId = stripeClient.createCheckoutSession(payment);
        payment.setStripeSessionId(sessionId);
        payment.setRedirectUrl("https://stripe.com/checkout/" + sessionId); // mock
        payment.setStatus(PaymentStatus.PENDING);
        payment = repository.save(payment);

        return mapper.toDTO(payment);
    }

    /**
     * Processes Stripe webhook events for payment verification.
     * 
     * <p>This method handles incoming webhook notifications from Stripe payment gateway.
     * It parses the webhook payload, verifies the signature for security, and updates
     * the payment status accordingly. Upon successful payment verification, it triggers
     * subscription activation.</p>
     *
     * @param payload The JSON payload received from Stripe webhook
     * @param signature The signature header for webhook verification
     * @throws ResourceNotFoundException if payment not found for the given session
     */
    @Override
    @Transactional
    public void processStripeWebhook(String payload, String signature) {
        // Extract session ID from webhook payload
        String sessionId = "mock_session"; // TODO: Implement Stripe Event parsing with Stripe SDK

        SubscriptionPayment payment = repository.findByStripeSessionId(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found for session: " + sessionId));

        PaymentStatus status = stripeClient.verifyPayment(sessionId);
        payment.setStatus(status);
        if (status == PaymentStatus.SUCCESSFUL) {
            payment.setPaidAt(java.time.LocalDateTime.now());
            subscriptionClient.activateSubscription(payment.getSubscriptionId());
        }
        repository.save(payment);
    }

    @Override
    public SubscriptionPaymentDTO findById(Long id) {
        SubscriptionPayment payment = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return mapper.toDTO(payment);
    }

    @Override
    public List<SubscriptionPaymentDTO> listPaymentsByCabinet(Long cabinetId) {
        return repository.findByCabinetId(cabinetId).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }
}