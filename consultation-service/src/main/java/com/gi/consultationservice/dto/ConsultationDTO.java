package com.gi.consultationservice.dto;

import com.gi.consultationservice.enums.ConsultationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime dateConsultation;
    private String examenClinique;
    private String examenSupplementaire;
    private String diagnostic;
    private String traitement;
    private String observations;
}
