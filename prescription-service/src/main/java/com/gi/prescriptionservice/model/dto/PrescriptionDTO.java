package com.gi.prescriptionservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    private Long id;
    
    @NotNull(message = "La consultation est obligatoire")
    @Positive(message = "ID de consultation invalide")
    private Long consultationId;
    
    @NotNull(message = "Le patient est obligatoire")
    @Positive(message = "ID du patient invalide")
    private Long patientId;
    
    @NotNull(message = "Le médecin est obligatoire")
    @Positive(message = "ID du médecin invalide")
    private Long doctorId;
    
    @NotNull(message = "Le cabinet est obligatoire")
    @Positive(message = "ID du cabinet invalide")
    private Long clinicId;
    
    @NotNull(message = "La date de prescription est obligatoire")
    private java.time.LocalDate prescriptionDate;
    
    @NotEmpty(message = "La prescription doit contenir au moins une ligne")
    @Valid
    private List<PrescriptionLineDTO> lines;
}