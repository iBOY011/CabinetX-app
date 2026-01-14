package com.gi.billingservice.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.gi.billingservice.client.ClinicClient;

@Component
public class ClinicClientImpl implements ClinicClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicClientImpl.class);
    private final WebClient.Builder webClientBuilder;

    public ClinicClientImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public ClinicDTO getClinic(Long clinicId) {
        try {
            LOGGER.info("Calling CLINIC-SERVICE for clinicId: {}", clinicId);
            ClinicDTO clinic = webClientBuilder.build()
                    .get()
                    .uri("http://CLINIC-SERVICE/api/clinics/{id}", clinicId)
                    .retrieve()
                    .bodyToMono(ClinicDTO.class)
                    .block();
            
            if (clinic != null) {
                LOGGER.info("Clinic received: id={}, name={}, address={}, phone={}", 
                    clinic.getId(), clinic.getName(), clinic.getAddress(), clinic.getPhone());
            } else {
                LOGGER.warn("Clinic {} not found or returned null", clinicId);
            }
            return clinic;
        } catch (Exception e) {
            LOGGER.error("Error fetching clinic {}: {}", clinicId, e.getMessage());
            return null;
        }
    }
}
