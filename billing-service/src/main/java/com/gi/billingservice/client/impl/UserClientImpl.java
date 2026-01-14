package com.gi.billingservice.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.gi.billingservice.client.UserClient;

@Component
public class UserClientImpl implements UserClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserClientImpl.class);
    private final WebClient.Builder webClientBuilder;

    public UserClientImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public UserDTO getUser(Long userId) {
        try {
            LOGGER.info("Calling USER-SERVICE for userId: {}", userId);
            UserDTO user = webClientBuilder.build()
                    .get()
                    .uri("http://USER-SERVICE/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
            
            if (user != null) {
                LOGGER.info("User received: id={}, firstName={}, lastName={}, login={}, role={}", 
                    user.getId(), user.getFirstName(), user.getLastName(), user.getLogin(), user.getRole());
            } else {
                LOGGER.warn("User {} not found or returned null", userId);
            }
            return user;
        } catch (Exception e) {
            LOGGER.error("Error fetching user {}: {}", userId, e.getMessage());
            return null;
        }
    }
}
