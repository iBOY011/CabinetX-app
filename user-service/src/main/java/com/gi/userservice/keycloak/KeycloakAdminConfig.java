package com.gi.userservice.keycloak;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring Boot pour activer les propriétés Keycloak Admin.
 * 
 * <p>Charge les propriétés depuis application.yml/application.properties :</p>
 * <pre>
 * keycloak.admin:
 *   server-url: https://keycloak.example.com
 *   realm: cabinetx-realm
 *   client-id: cabinetx-admin-client
 *   client-secret: xxxx-yyyy-zzzz
 * </pre>
 * 
 * <p><b>Rôle :</b> Active KeycloakAdminProperties via @EnableConfigurationProperties.</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableConfigurationProperties(KeycloakAdminProperties.class)
public class KeycloakAdminConfig {
}
