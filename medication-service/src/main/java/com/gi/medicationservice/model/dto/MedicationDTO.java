package com.gi.medicationservice.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {

    private Long id;

    @NotBlank(message = "Le nom commercial est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom commercial doit contenir entre 2 et 100 caractères")
    private String commercialName;

    @NotBlank(message = "La DCI (Dénomination Commune Internationale) est obligatoire")
    @Size(min = 2, max = 100, message = "La DCI doit contenir entre 2 et 100 caractères")
    private String dci;

    @NotBlank(message = "Le dosage est obligatoire")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?\\s*(mg|g|ml|L|mcg|µg|UI|%)?$", 
             message = "Format de dosage invalide (ex: 500mg, 10ml, 2.5g)")
    private String dosage;

    @NotBlank(message = "La forme pharmaceutique est obligatoire")
    @Size(min = 2, max = 50, message = "La forme doit contenir entre 2 et 50 caractères")
    private String form;
}