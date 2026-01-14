package com.gi.billingservice.model.dto.request;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ConsultationDTO {
    private Long id;
    private Long patientId;
    private Long medecinId;
    private Long cabinetId;
    private String diagnostic;
    private String traitement;
    private OffsetDateTime dateConsultation;
    
}