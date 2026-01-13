package com.gi.prescriptionservice.model.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Signature numérique (PNG base64) optionnelle
    private String digitalSignature;
    
    @NotEmpty(message = "La prescription doit contenir au moins une ligne")
    @Valid
    private List<PrescriptionLineDTO> lines;
}