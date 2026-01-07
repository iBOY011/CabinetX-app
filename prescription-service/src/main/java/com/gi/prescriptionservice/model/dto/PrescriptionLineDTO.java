package com.gi.prescriptionservice.model.dto;

import com.gi.prescriptionservice.model.enums.PrescriptionLineType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionLineDTO {

    private Long id;
    
    @Positive(message = "ID de prescription invalide")
    private Long prescriptionId;
    
    @NotNull(message = "Le type de ligne est obligatoire")
    private PrescriptionLineType lineType;
    
    @NotNull(message = "Le médicament est obligatoire")
    @Positive(message = "ID du médicament invalide")
    private Long medicationId;
    
    @NotBlank(message = "Le nom du médicament est obligatoire")
    @Size(max = 100, message = "Le nom du médicament est trop long (max 100 caractères)")
    private String medicationName;
    
    @NotBlank(message = "Le dosage est obligatoire")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?\\s*(mg|g|ml|L|mcg|µg|UI|%)?$", 
             message = "Format de dosage invalide (ex: 500mg, 10ml, 2.5g)")
    private String dosage;
    
    @Positive(message = "La durée doit être positive")
    @Max(value = 365, message = "La durée ne peut pas dépasser 365 jours")
    private int durationDays;
    
    @Size(max = 500, message = "Le commentaire est trop long (max 500 caractères)")
    private String comment;
}