package com.gi.consultationservice.dto;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.gi.consultationservice.enums.ConsultationType;

import jakarta.validation.constraints.*;
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
    
    @NotNull(message = "Le rendez-vous est obligatoire")
    @Positive(message = "ID du rendez-vous invalide")
    private Long rendezVousId;
    
    @NotNull(message = "Le patient est obligatoire")
    @Positive(message = "ID du patient invalide")
    private Long patientId;
    
    @NotNull(message = "Le médecin est obligatoire")
    @Positive(message = "ID du médecin invalide")
    private Long medecinId;
    
    @NotNull(message = "Le cabinet est obligatoire")
    @Positive(message = "ID du cabinet invalide")
    private Long cabinetId;
    
    @NotNull(message = "Le type de consultation est obligatoire")
    private ConsultationType type;
    
    @NotNull(message = "La date de consultation est obligatoire")
    private OffsetDateTime dateConsultation;
    
    @Size(max = 2000, message = "L'examen clinique est trop long (max 2000 caractères)")
    private String examenClinique;
    
    @Size(max = 2000, message = "L'examen supplémentaire est trop long (max 2000 caractères)")
    private String examenSupplementaire;
    
    @Size(max = 1000, message = "Le diagnostic est trop long (max 1000 caractères)")
    private String diagnostic;
    
    @Size(max = 2000, message = "Le traitement est trop long (max 2000 caractères)")
    private String traitement;
    
    @Size(max = 1000, message = "Les observations sont trop longues (max 1000 caractères)")
    private String observations;
    
    private Boolean archived;
    private ZonedDateTime archivedAt;
}
