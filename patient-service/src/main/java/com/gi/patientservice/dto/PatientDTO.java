package com.gi.patientservice.dto;

import com.gi.patientservice.entities.Adresse;
import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private String cin;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String numTel;
    private TypeMutuelle typeMutuelle;
    private Long cabinetId;
    private Adresse adresse;
    private LocalDateTime createdAt;
}
