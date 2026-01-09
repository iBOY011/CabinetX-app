package com.gi.consultationservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                
                if (authentication instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                    Jwt jwt = jwtAuth.getToken();
                    String tokenValue = jwt.getTokenValue();
                    
                    System.out.println("[FeignClientConfig] Adding Authorization header to Feign request: " + template.url());
                    template.header("Authorization", "Bearer " + tokenValue);
                } else {
                    System.out.println("[FeignClientConfig] WARNING: No JWT token available for Feign request: " + template.url());
                }
            }
        };
    }
}
