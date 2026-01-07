package com.gi.patientservice.dto;

import com.gi.patientservice.entities.Adresse;
import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    
    @NotBlank(message = "Le CIN est obligatoire")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9]{5,7}$", message = "Format de CIN invalide (ex: AB123456)")
    private String cin;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom contient des caractères invalides")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom contient des caractères invalides")
    private String prenom;
    
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;
    
    @NotNull(message = "Le sexe est obligatoire")
    private Sexe sexe;
    
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^(\\+212|0)[5-7]\\d{8}$", message = "Format de téléphone invalide (ex: 0612345678 ou +212612345678)")
    private String numTel;
    
    private TypeMutuelle typeMutuelle;
    
    @NotNull(message = "Le cabinet est obligatoire")
    @Positive(message = "ID du cabinet invalide")
    private Long cabinetId;
    
    private Adresse adresse;
    private LocalDateTime createdAt;
}
