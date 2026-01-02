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
                    .authorizeHttpRequests(ar -> ar.requestMatchers("/api/**").hasAuthority("ADMIN"))
                    .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                    .oauth2ResourceServer(o2 -> o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
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


