package com.gi.appointmentservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.appointmentservice.Model.DTO.UserInfoDTO;

import java.util.List;

@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Get all users by clinic ID
     */
    public List<UserInfoDTO> getUsersByClinic(Long clinicId) {
        System.out.println("[UserClient] Calling user service for clinic ID: " + clinicId);

        List<UserInfoDTO> users = webClient.get()
                .uri("http://USER-SERVICE/api/users/clinic/{clinicId}", clinicId)
                .retrieve()
                .bodyToFlux(UserInfoDTO.class)
                .collectList()
                .block();

        System.out.println("[UserClient] Received " + (users != null ? users.size() : 0) + " users for clinic " + clinicId);
        return users;
    }

    /**
     * Get a specific user by ID
     */
    public UserInfoDTO getUserById(Long userId) {
        System.out.println("[UserClient] Calling user service for user ID: " + userId);

        UserInfoDTO user = webClient.get()
                .uri("http://USER-SERVICE/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserInfoDTO.class)
                .block();

        System.out.println("[UserClient] Received user: " + (user != null ? user.getLogin() : "null"));
        return user;
    }
}
