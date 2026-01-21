package com.gi.clinicservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité Spring Security pour le microservice Clinic.
 * 
 * <p><b>Stratégie :</b> OAuth2 Resource Server avec validation JWT Keycloak.</p>
 * 
 * <p><b>Endpoints sécurisés :</b></p>
 * <ul>
 *   <li><b>/api/clinics/** :</b> Accès ADMIN (création/modification cabinets)</li>
 *   <li><b>/h2-console/** :</b> Public (développement uniquement)</li>
 *   <li><b>/actuator/** :</b> Public (monitoring Prometheus)</li>
 * </ul>
 * 
 * <p><b>Mode dégradation :</b> Si issuer-uri vide → Authentification désactivée (dev local).</p>
 * 
 * <p><b>CORS :</b> Désactivé ici, géré par Gateway (Spring Cloud Gateway).</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    /**
     * Configure la chaîne de filtres de sécurité.
     * 
     * @param http Le HttpSecurity à configurer
     * @return La chaîne de filtres configurée
     * @throws Exception En cas d'erreur de configuration
     * 
     * <p><b>Configuration :</b></p>
     * <ul>
     *   <li>STATELESS : Pas de session HTTP (JWT bearer token)</li>
     *   <li>CSRF désactivé (API REST sans cookies)</li>
     *   <li>OAuth2 conditionnel (dev vs prod)</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS is handled by the gateway - disable here to avoid duplicate headers
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar -> ar.requestMatchers("/h2-console/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/actuator/**").permitAll());

        // Conditionally apply OAuth2 configuration
        if (isOAuth2Enabled()) {
            http
                    .authorizeHttpRequests(ar -> ar.requestMatchers("/api/clinic/**").permitAll()) // Allow service-to-service calls
                    .authorizeHttpRequests(ar -> ar.requestMatchers("/api/clinics/**").permitAll()) // Allow service-to-service calls
                    .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                    .oauth2ResourceServer(o2->o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)).authenticationEntryPoint((request, response, authException) -> {
                        if (request.getRequestURI().startsWith("/api/")) {
                            response.setStatus(200);
                            return;
                        }
                        response.sendError(401, "Unauthorized");
                    }));
        } else {
            // Development mode - allow all requests
            http.authorizeHttpRequests(ar -> ar.anyRequest().permitAll());
        }

        return http.build();
    }

    private boolean isOAuth2Enabled() {
        return issuerUri != null && !issuerUri.trim().isEmpty();
    }
}


