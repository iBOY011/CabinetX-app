package com.gi.consultationservice.dto;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.gi.consultationservice.enums.ConsultationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {
    private Long id;
    private Long rendezVousId;
    private Long patientId;
    private Long medecinId;
    private Long cabinetId;
    private ConsultationType type;
    private OffsetDateTime dateConsultation;
    private String examenClinique;
    private String examenSupplementaire;
    private String diagnostic;
    private String traitement;
    private String observations;
    private Boolean archived;
    private ZonedDateTime archivedAt;
}
