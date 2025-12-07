package com.gi.medicalrecordservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DossierMedicalDTO {
    private Long id;
    private Long patientId;
    private String antecedents;
    private String allergies;
    private String traitements;
    private String habitudes;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereMiseAJour;
    private List<DocumentMedicalDTO> documents;
    private List<ConsultationInfo> historiqueConsultations;
}
