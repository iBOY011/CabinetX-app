package com.gi.medicalrecordservice.model.dto.response;

import jakarta.validation.constraints.*;
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
public class MedicalRecordDTO {
    private Long id;
    
    @NotNull(message = "Le patient est obligatoire")
    @Positive(message = "ID du patient invalide")
    private Long patientId;
    
    @Size(max = 2000, message = "Les antécédents médicaux sont trop longs (max 2000 caractères)")
    private String medicalHistory;
    
    @Size(max = 1000, message = "Les allergies sont trop longues (max 1000 caractères)")
    private String allergies;
    
    @Size(max = 2000, message = "Les traitements sont trop longs (max 2000 caractères)")
    private String treatments;
    
    @Size(max = 1000, message = "Les habitudes sont trop longues (max 1000 caractères)")
    private String habits;
    
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    private List<MedicalDocumentDTO> documents;
    private List<ConsultationInfo> consultationHistory;
}