package com.gi.userservice.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité Spring Security pour le microservice User.
 * 
 * <p><b>Stratégie double :</b></p>
 * <ol>
 *   <li><b>Chaîne 1 (@Order(1)) :</b> /api/** → PAS d'OAuth2 (appels inter-services)</li>
 *   <li><b>Chaîne 2 (@Order(2)) :</b> Autres routes → OAuth2 JWT Keycloak</li>
 * </ol>
 * 
 * <p><b>Endpoints /api/** (sans auth) :</b></p>
 * <ul>
 *   <li>/api/users/login/{login} - Appel depuis API Gateway après login Keycloak</li>
 *   <li>/api/users/clinic/{id} - Appel depuis Appointment/Consultation services</li>
 *   <li>/api/users/{id} - Appel depuis autres microservices</li>
 * </ul>
 * 
 * <p><b>Endpoints OAuth2 (avec auth) :</b></p>
 * <ul>
 *   <li>/h2-console/** - Public (dev uniquement)</li>
 *   <li>/actuator/** - Public (monitoring)</li>
 *   <li>Autres - AUTHENTICATED (JWT bearer token requis)</li>
 * </ul>
 * 
 * <p><b>Mode dégradation :</b></p>
 * <ul>
 *   <li>Si security.enabled=false → Toute la config désactivée (@ConditionalOnProperty)</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {
    private JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    // Higher priority filter chain for /api/** - no OAuth2 required
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ar -> ar.anyRequest().permitAll())
                .build();
    }

    // Default filter chain for all other requests - OAuth2 required
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(request -> !request.getRequestURI().startsWith("/api/"))
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar -> ar
                        .requestMatchers("/h2-console/**", "/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o2 -> o2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                )
                .build();
    }
}
