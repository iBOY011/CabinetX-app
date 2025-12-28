package com.gi.paymentservice.client;

import org.springframework.stereotype.Service;

@Service
public class SubscriptionClient {

    public void activateSubscription(Long subscriptionId) {
        // TODO: Call subscription service to activate subscription
        // For now, mock implementation
        System.out.println("Activating subscription: " + subscriptionId);
    }
}