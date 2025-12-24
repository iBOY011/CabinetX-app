package com.gi.appointmentservice.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;


@Service
public class PatientClient {

    private final WebClient webClient;

    public PatientClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }


    public PatientInfoDTO getPatientById(Long id) {
    System.out.println("Calling patient service for ID: " + id);

    PatientInfoDTO patient = webClient.get()
            .uri("http://PATIENT-SERVICE/api/patients/{id}", id)
            .retrieve()
            .bodyToMono(PatientInfoDTO.class)
            .block(); // blocking only for testing

    System.out.println("Received patient: " + patient);
    return patient;
}
}