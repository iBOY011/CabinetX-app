package com.gi.userservice.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gi.userservice.model.dto.request.CreateUserRequest;
import com.gi.userservice.model.enums.UserRole;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KeycloakAdminClient {

    private final KeycloakAdminProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KeycloakAdminClient(KeycloakAdminProperties properties, RestTemplateBuilder restTemplateBuilder) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder.build();
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    public String createUser(CreateUserRequest request) {
        requireConfigured();
        String token = getAccessToken();

        HttpHeaders headers = authHeaders(token);
        Map<String, Object> body = new HashMap<>();
        body.put("username", request.getLogin());
        body.put("firstName", request.getFirstName());
        body.put("lastName", request.getLastName());
        body.put("email", request.getLogin());
        body.put("enabled", Boolean.TRUE);
        body.put("emailVerified", Boolean.FALSE);
        body.put("requiredActions", List.of("UPDATE_PASSWORD", "VERIFY_EMAIL"));

        ResponseEntity<Void> response = restTemplate.exchange(usersUrl(), HttpMethod.POST, new HttpEntity<>(body, headers), Void.class);
        if (response.getStatusCode() == HttpStatus.CONFLICT) {
            return findUserIdByUsername(request.getLogin(), token)
                    .orElseThrow(() -> new IllegalStateException("Keycloak user already exists but id could not be resolved"));
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Keycloak user creation failed with status " + response.getStatusCode());
        }
        String userId = extractUserId(response.getHeaders(), token, request.getLogin());
        try {
            assignRealmRole(userId, request.getRole(), token);
        } catch (RuntimeException ex) {
            deleteUser(userId, token); // rollback KC user if role assignment fails
            throw ex;
        }
        return userId;
    }

    public void deleteUser(String keycloakUserId) {
        if (!StringUtils.hasText(keycloakUserId) || !isEnabled()) {
            return;
        }
        String token = getAccessToken();
        deleteUser(keycloakUserId, token);
    }

    private void deleteUser(String keycloakUserId, String token) {
        if (!StringUtils.hasText(keycloakUserId)) {
            return;
        }
        HttpHeaders headers = authHeaders(token);
        restTemplate.exchange(usersUrl() + "/" + keycloakUserId, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
    }

    private String extractUserId(HttpHeaders headers, String token, String username) {
        List<String> locations = headers.get(HttpHeaders.LOCATION);
        if (locations != null && !locations.isEmpty()) {
            String location = locations.get(0);
            int slashIndex = location.lastIndexOf('/');
            if (slashIndex >= 0 && slashIndex < location.length() - 1) {
                return location.substring(slashIndex + 1);
            }
        }
        return findUserIdByUsername(username, token)
                .orElseThrow(() -> new IllegalStateException("Keycloak user id could not be determined"));
    }

    private Optional<String> findUserIdByUsername(String username, String token) {
        HttpHeaders headers = authHeaders(token);
        String uri = UriComponentsBuilder.fromHttpUrl(usersUrl())
                .queryParam("username", username)
                .toUriString();
        ResponseEntity<String> response = restTemplate.exchange(URI.create(uri), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return Optional.empty();
        }
        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            if (node.isArray() && node.size() > 0) {
                JsonNode first = node.get(0);
                JsonNode idNode = first.get("id");
                if (idNode != null && idNode.isTextual()) {
                    return Optional.of(idNode.asText());
                }
            }
        } catch (IOException ignored) {
            // ignore parsing issue and fall through to empty
        }
        return Optional.empty();
    }

    private String getAccessToken() {
        requireConfigured();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl(), new HttpEntity<>(form, headers), String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Keycloak admin token retrieval failed with status " + response.getStatusCode());
        }
        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            JsonNode tokenNode = node.get("access_token");
            if (tokenNode != null && tokenNode.isTextual()) {
                return tokenNode.asText();
            }
        } catch (IOException ignored) {
            // ignore
        }
        throw new IllegalStateException("Keycloak admin token missing in response");
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private String tokenUrl() {
        return properties.getServerUrl() + "/realms/" + properties.getRealm() + "/protocol/openid-connect/token";
    }

    private String usersUrl() {
        return properties.getServerUrl() + "/admin/realms/" + properties.getRealm() + "/users";
    }

    private String rolesUrl() {
        return properties.getServerUrl() + "/admin/realms/" + properties.getRealm() + "/roles";
    }

    private void requireConfigured() {
        if (!properties.isConfigured()) {
            throw new IllegalStateException("Keycloak admin client is not configured");
        }
    }

    private void assignRealmRole(String userId, UserRole role, String token) {
        if (role == null) {
            return;
        }
        HttpHeaders headers = authHeaders(token);
        RoleRepresentation roleRep = resolveRealmRole(role.name(), headers);
        HttpEntity<List<RoleRepresentation>> entity = new HttpEntity<>(List.of(roleRep), headers);
        ResponseEntity<Void> response = restTemplate.exchange(usersUrl() + "/" + userId + "/role-mappings/realm",
                HttpMethod.POST,
                entity,
                Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Assigning role " + role.name() + " failed with status " + response.getStatusCode());
        }
    }

    private RoleRepresentation resolveRealmRole(String roleName, HttpHeaders headers) {
        ResponseEntity<String> response = restTemplate.exchange(rolesUrl() + "/" + roleName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new IllegalStateException("Keycloak role '" + roleName + "' not found in realm '" + properties.getRealm() + "'");
        }
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Could not resolve Keycloak role '" + roleName + "', status " + response.getStatusCode());
        }
        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            JsonNode idNode = node.get("id");
            JsonNode nameNode = node.get("name");
            if (idNode == null || nameNode == null || !idNode.isTextual() || !nameNode.isTextual()) {
                throw new IllegalStateException("Role representation for '" + roleName + "' is missing id or name");
            }
            RoleRepresentation role = new RoleRepresentation();
            role.setId(idNode.asText());
            role.setName(nameNode.asText());
            return role;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse Keycloak role representation for '" + roleName + "'", ex);
        }
    }

    private static class RoleRepresentation {
        private String id;
        private String name;
        private Boolean composite;
        private Boolean clientRole;
        private String containerId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getComposite() {
            return composite;
        }

        public void setComposite(Boolean composite) {
            this.composite = composite;
        }

        public Boolean getClientRole() {
            return clientRole;
        }

        public void setClientRole(Boolean clientRole) {
            this.clientRole = clientRole;
        }

        public String getContainerId() {
            return containerId;
        }

        public void setContainerId(String containerId) {
            this.containerId = containerId;
        }
    }
}
