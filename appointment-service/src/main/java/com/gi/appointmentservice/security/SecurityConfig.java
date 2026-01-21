package com.gi.appointmentservice.security;

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
 * Configuration de sécurité Spring Security pour le microservice Appointment.
 * 
 * <p>Configure l'authentification OAuth2/JWT avec Keycloak :
 * <ul>
 *   <li>Sessions STATELESS (authentification par token JWT uniquement)</li>
 *   <li>CSRF désactivé (API REST sans session)</li>
 *   <li>OAuth2 Resource Server avec conversion JWT personnalisée</li>
 *   <li>Extraction des rôles depuis les claims Keycloak</li>
 * </ul>
 * 
 * <p>Architecture de sécurité :
 * <ul>
 *   <li>Gateway : Point d'entrée, validation initiale des tokens</li>
 *   <li>Microservices : Validation et extraction des rôles via JwtAuthConverter</li>
 *   <li>Méthodes : Protection fine via @PreAuthorize("hasRole('MEDECIN')")</li>
 * </ul>
 * 
 * <p>Note : Tous les endpoints /api/** sont actuellement en permitAll() pour faciliter
 * le développement. En production, appliquer des rôles spécifiques :
 * <ul>
 *   <li>SECRETAIRE : Gestion des rendez-vous et files d'attente</li>
 *   <li>MEDECIN : Consultation, modification de statuts</li>
 *   <li>PATIENT : Consultation de ses propres rendez-vous uniquement</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    /**
     * Configure la chaîne de filtres de sécurité avec OAuth2/JWT.
     * 
     * <p>Configuration appliquée :
     * <ul>
     *   <li>Pas de sessions (STATELESS)</li>
     *   <li>CSRF désactivé</li>
     *   <li>Frame Options désactivées (pour H2 console en dev)</li>
     *   <li>OAuth2 Resource Server avec JwtAuthConverter pour extraction des rôles</li>
     * </ul>
     * 
     * @param http l'objet HttpSecurity pour configurer la sécurité
     * @return la chaîne de filtres configurée
     * @throws Exception si erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/**").permitAll())
                .authorizeHttpRequests(ar -> ar.anyRequest().permitAll())
                .oauth2ResourceServer(o2 -> o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .build();
    }

}
