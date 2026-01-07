package com.gi.appointmentservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.appointmentservice.Model.DTO.CabinetInfoDTO;

/**
 * Client to communicate with clinic-service to get cabinet information
 */
@Service
public class CabinetClient {

    private final WebClient webClient;

    public CabinetClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Get cabinet information by ID
     * 
     * @param cabinetId the ID of the cabinet
     * @return CabinetInfoDTO with cabinet details including doctorId
     */
    public CabinetInfoDTO getCabinetById(Long cabinetId) {
        System.out.println("Calling clinic-service for cabinet ID: " + cabinetId);

        CabinetInfoDTO cabinet = webClient.get()
                .uri("http://CLINIC-SERVICE/api/clinic/{id}", cabinetId)
                .retrieve()
                .bodyToMono(CabinetInfoDTO.class)
                .block(); // blocking for simplicity

        System.out.println("Received cabinet: " + cabinet);
        return cabinet;
    }
}
