package com.gi.patientservice.security;

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
 * Configuration de sécurité Spring Security pour le microservice Patient.
 * 
 * <p>Configuration adaptée à une architecture microservices avec passerelle API :
 * <ul>
 *   <li>CORS désactivé (géré par la gateway)</li>
 *   <li>Sessions STATELESS (authentification JWT)</li>
 *   <li>CSRF désactivé (API REST sans session)</li>
 *   <li>Tous les endpoints /api/** accessibles (autorisation gérée par gateway)</li>
 * </ul>
 * 
 * <p>Note : En production, la gateway (Spring Cloud Gateway) gère l'authentification
 * OAuth2/JWT et transfère les requêtes authentifiées vers ce service. Les microservices
 * font confiance aux requêtes de la gateway (architecture de sécurité périmétrique).
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité.
     * 
     * <p>Stratégie de sécurité :
     * <ul>
     *   <li>Console H2 : accès libre (environnement dev uniquement)</li>
     *   <li>API /api/** : accès libre (sécurisé au niveau gateway)</li>
     *   <li>Autres endpoints : accès libre par défaut</li>
     * </ul>
     * 
     * @param http l'objet HttpSecurity pour configurer la sécurité
     * @return la chaîne de filtres configurée
     * @throws Exception si erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CORS is handled by the gateway - disable here to avoid duplicate headers
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar -> ar
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll())
                .build();
    }

}
