package com.gi.notificationservice.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8102/api/users";

    public List<UserDTO> getUsersByCabinetAndRole(Long cabinetId, String role) {
        String url = USER_SERVICE_URL + "/by-cabinet-and-role?cabinetId=" + cabinetId + "&role=" + role;
        System.out.println("[UserClient] Fetching users from: " + url);
        
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserDTO>>() {}
        );
        
        List<UserDTO> users = response.getBody();
        System.out.println("[UserClient] Retrieved " + (users != null ? users.size() : 0) + " users");
        return users;
    }

    @Data
    public static class UserDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String login;
        private String role;
        private Long cabinetId;
    }
}
