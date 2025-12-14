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

    @Override
    @Transactional
    public void processStripeWebhook(String payload, String signature) {
        // TODO: Parse payload, verify signature
        // For now, assume sessionId from payload
        String sessionId = "mock_session"; // extract from payload

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