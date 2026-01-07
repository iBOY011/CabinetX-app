package com.gi.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time notifications
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker
        // Messages sent to /topic will be broadcast to all subscribers
        registry.enableSimpleBroker("/topic");

        // Messages sent to /app will be routed to @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws-notifications endpoint for WebSocket connections
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:3000") // Frontend URL
                .withSockJS(); // Enable SockJS fallback for browsers that don't support WebSocket
    }
}
