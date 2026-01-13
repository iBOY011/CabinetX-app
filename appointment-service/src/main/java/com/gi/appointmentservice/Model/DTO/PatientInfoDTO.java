package com.gi.appointmentservice.Model.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PatientInfoDTO {
    Long id;
    String prenom;
    String nom;
    String cin;
    LocalDate dateNaissance;

}
