package com.gi.billingservice.client.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.billingservice.client.ConsultationClient;
import com.gi.billingservice.model.dto.request.ConsultationDTO;

@Service
public class ConsultationClientImpl implements ConsultationClient {

    private final WebClient webClient;

    public ConsultationClientImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public ConsultationDTO getConsultation(Long id) {
        System.out.println("Calling consultation service for ID: " + id);

        ConsultationDTO consultation = webClient.get()
                .uri("http://CONSULTATION-SERVICE/api/consultations/{id}", id)
                .retrieve()
                .bodyToMono(ConsultationDTO.class)
                .block(); // blocking only for testing

        System.out.println("Received consultation: " + consultation);
        return consultation;
    }
}
