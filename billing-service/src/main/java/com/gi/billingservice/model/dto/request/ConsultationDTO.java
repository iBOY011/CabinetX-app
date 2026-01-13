package com.gi.billingservice.model.dto.request;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ConsultationDTO {
    private Long id;
    private Long patientId;
    private Long medecinId;
    private Long cabinetId;
    private OffsetDateTime dateConsultation;
    
}