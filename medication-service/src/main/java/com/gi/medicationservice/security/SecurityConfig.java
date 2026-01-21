package com.gi.medicationservice.security;

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
 * Configuration de sécurité Spring Security pour le microservice Medication.
 * 
 * <p><b>Stratégie :</b> OAuth2 Resource Server avec validation JWT Keycloak.</p>
 * 
 * <p><b>Endpoints sécurisés :</b></p>
 * <ul>
 *   <li><b>/api/medications/** :</b> Public (appels inter-services depuis Prescription Service)</li>
 *   <li><b>/h2-console/** :</b> Public (développement uniquement)</li>
 *   <li><b>Autres :</b> AUTHENTICATED (JWT bearer token requis)</li>
 * </ul>
 * 
 * <p><b>Permissions métier :</b></p>
 * <ul>
 *   <li>MEDECIN : GET /autocomplete, GET /{id} (lecture seule pour ordonnances)</li>
 *   <li>ADMIN : POST, PUT, DELETE (gestion catalogue)</li>
 * </ul>
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
    private JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CORS is handled by the gateway - disable here to avoid duplicate headers
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h->h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar->ar.requestMatchers("/h2-console/**").permitAll())
                .authorizeHttpRequests(ar->ar.requestMatchers("/api/**").permitAll())
                .authorizeHttpRequests(ar->ar.anyRequest().authenticated())
                .oauth2ResourceServer(o2->o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .build();
    }
}


