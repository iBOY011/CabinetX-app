package com.gi.consultationservice.security;

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
                .authorizeHttpRequests(ar->ar.requestMatchers("/h2-console/**", "/graphiql/**", "/graphql/**").permitAll())
                .authorizeHttpRequests(ar->ar.requestMatchers("/api/**").permitAll())
                .authorizeHttpRequests(ar->ar.anyRequest().authenticated())
                // Disable OAuth2 for permitAll endpoints
                .oauth2ResourceServer(o2->o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)).authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(200); // Allow through for inter-service calls
                        return;
                    }
                    response.sendError(401, "Unauthorized");
                }))
                .build();
    }
}


