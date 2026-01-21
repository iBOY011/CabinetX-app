package com.gi.analyticsservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de sécurité Spring Security pour le microservice Analytics.
 * 
 * <p>Configure l'authentification OAuth2/JWT avec restrictions strictes :
 * <ul>
 *   <li>Sessions STATELESS (authentification par token JWT uniquement)</li>
 *   <li>CSRF désactivé (API REST sans session)</li>
 *   <li>CORS configuré pour autoriser les appels depuis le frontend</li>
 *   <li>Accès restreint au rôle ADMIN pour tous les endpoints /api/**</li>
 * </ul>
 * 
 * <p>Architecture de sécurité :
 * <ul>
 *   <li>Gateway : Point d'entrée, validation initiale des tokens</li>
 *   <li>Analytics Service : Validation JWT + vérification rôle ADMIN</li>
 *   <li>Console H2 : Accès libre (environnement dev uniquement)</li>
 * </ul>
 * 
 * <p>Autorisation :
 * <ul>
 *   <li>/h2-console/** : Accès libre (dev)</li>
 *   <li>/api/** : Rôle ADMIN requis (super-admin plateforme uniquement)</li>
 *   <li>Autres : Authentification requise</li>
 * </ul>
 * 
 * <p>Justification accès ADMIN uniquement :
 * Les statistiques globales et KPI par cabinet contiennent des données sensibles
 * (revenus, performance) réservées aux administrateurs de la plateforme.
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
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h->h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar->ar.requestMatchers("/h2-console/**").permitAll())
                .authorizeHttpRequests(ar->ar.requestMatchers("/api/**").hasAuthority("ADMIN"))
                .authorizeHttpRequests(ar->ar.anyRequest().authenticated())
                .oauth2ResourceServer(o2->o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // IMPORTANT : Utilisez setAllowedOriginPatterns au lieu de setAllowedOrigins
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Ou pour plus de sécurité, spécifiez les origines exactes :
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "http://localhost:3000",
        //     "http://localhost:8081",
        //     "http://localhost:4200"
        // ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true); // IMPORTANT : Ajoutez cette ligne

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


