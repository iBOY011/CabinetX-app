package com.gi.billingservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(jwtTokenRelayFilter());
    }

    private ExchangeFilterFunction jwtTokenRelayFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                Jwt jwt = jwtAuth.getToken();
                String tokenValue = jwt.getTokenValue();
                
                System.out.println("[WebClientConfig] Adding Authorization header to request: " + clientRequest.url());
                
                ClientRequest newRequest = ClientRequest.from(clientRequest)
                        .header("Authorization", "Bearer " + tokenValue)
                        .build();
                
                return Mono.just(newRequest);
            } else {
                System.out.println("[WebClientConfig] WARNING: No JWT token available for request: " + clientRequest.url());
                return Mono.just(clientRequest);
            }
        });
    }
}
