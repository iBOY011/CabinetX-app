package com.gi.paymentservice.client;

import org.springframework.stereotype.Service;

/**
 * Feign client for communicating with the Subscription Management Service.
 * 
 * <p>This client provides methods to interact with subscription lifecycle operations
 * such as activation, renewal, and cancellation. It uses Spring Cloud OpenFeign for
 * inter-service communication in the microservices architecture.</p>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 */
@Service
public class SubscriptionClient {

    /**
     * Activates a subscription after successful payment verification.
     * 
     * <p>This method sends a request to the Subscription Service to activate
     * a pending subscription. The activation grants access to premium features
     * based on the subscription plan.</p>
     * 
     * <p><strong>Note:</strong> This is a temporary implementation using direct
     * service calls. Will be replaced with Feign client once Subscription Service
     * is fully implemented.</p>
     *
     * @param subscriptionId The unique identifier of the subscription to activate
     */
    public void activateSubscription(Long subscriptionId) {
        // Will be implemented with @FeignClient annotation pointing to subscription-service
        // Example: POST /api/subscriptions/{id}/activate
        System.out.println("Activating subscription: " + subscriptionId);
    }
}