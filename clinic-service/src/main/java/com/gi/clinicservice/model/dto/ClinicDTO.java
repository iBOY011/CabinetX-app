package com.gi.clinicservice.model.dto;

import com.gi.clinicservice.model.enums.ClinicStatus;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {

    private Long id;

    @NotBlank(message = "Le nom du cabinet est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @NotBlank(message = "La spécialité est obligatoire")
    @Size(min = 2, max = 100, message = "La spécialité doit contenir entre 2 et 100 caractères")
    private String specialty;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^(\\+212|0)[5-7]\\d{8}$", 
             message = "Format de téléphone invalide (ex: 0612345678 ou +212612345678)")
    private String phone;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 200, message = "L'adresse doit contenir entre 5 et 200 caractères")
    private String address;

    @Pattern(regexp = "^(https?:\\/\\/)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*\\/?$", 
             message = "Format d'URL invalide")
    private String logoUrl;

    @NotNull(message = "Le statut est obligatoire")
    private ClinicStatus status = ClinicStatus.ACTIVE;

    @NotNull(message = "La date de fin de service est obligatoire")
    @Future(message = "La date de fin de service doit être dans le futur")
    private LocalDate serviceEndDate;
}