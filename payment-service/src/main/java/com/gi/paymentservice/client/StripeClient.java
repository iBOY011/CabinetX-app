package com.gi.paymentservice.client;

import com.gi.paymentservice.model.entity.SubscriptionPayment;
import com.gi.paymentservice.model.enums.PaymentStatus;
import org.springframework.stereotype.Service;

@Service
public class StripeClient {

    public String createCheckoutSession(SubscriptionPayment payment) {
        // TODO: Integrate with Stripe API to create checkout session
        // For now, return a mock session ID
        return "cs_mock_" + payment.getId();
    }

    public PaymentStatus verifyPayment(String sessionId) {
        // TODO: Integrate with Stripe API to verify payment status
        // For now, return SUCCESSFUL as mock
        return PaymentStatus.SUCCESSFUL;
    }
}