package com.gi.billingservice.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.billingservice.client.ConsultationClient;
import com.gi.billingservice.model.dto.request.ConsultationDTO;

@Service
public class ConsultationClientImpl implements ConsultationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationClientImpl.class);
    private final WebClient webClient;

    public ConsultationClientImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public ConsultationDTO getConsultation(Long id) {
        LOGGER.info("Calling consultation service for ID: {}", id);

        try {
            ConsultationDTO consultation = webClient.get()
                    .uri("http://CONSULTATION-SERVICE/api/consultations/{id}", id)
                    .retrieve()
                    .bodyToMono(ConsultationDTO.class)
                    .block();

            LOGGER.info("Received consultation: medecinId={}, cabinetId={}, type={}, diagnostic={}", 
                    consultation != null ? consultation.getMedecinId() : null,
                    consultation != null ? consultation.getCabinetId() : null,
                    consultation != null ? consultation.getType() : null,
                    consultation != null ? consultation.getDiagnostic() : null);
            return consultation;
        } catch (Exception e) {
            LOGGER.error("Error fetching consultation: {}", e.getMessage());
            return null;
        }
    }
}
