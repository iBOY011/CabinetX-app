package com.gi.billingservice.client.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.billingservice.client.PatientClient;

@Service
public class PatientClientImpl implements PatientClient {

    private final WebClient webClient;

    public PatientClientImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public String getPatientName(Long id) {
        try {
            PatientDTO patient = webClient.get()
                    .uri("http://PATIENT-SERVICE/api/patients/{id}", id)
                    .retrieve()
                    .bodyToMono(PatientDTO.class)
                    .block();
            
            if (patient != null) {
                return patient.getPrenom() + " " + patient.getNom();
            }
        } catch (Exception e) {
            System.err.println("Error fetching patient: " + e.getMessage());
        }
        
        return "Patient #" + id;
    }
    
    // DTO local pour la réponse
    private static class PatientDTO {
        private String prenom;
        private String nom;
        
        public String getPrenom() {
            return prenom;
        }
        
        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }
        
        public String getNom() {
            return nom;
        }
        
        public void setNom(String nom) {
            this.nom = nom;
        }
    }
}
