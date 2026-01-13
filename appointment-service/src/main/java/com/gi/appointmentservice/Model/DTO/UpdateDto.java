package com.gi.appointmentservice.Model.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import com.gi.appointmentservice.Model.Enum.MotifRDV;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateDto {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;
    
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime Heure_debut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime Heure_fin;
    
    @NotNull(message = "Le motif du rendez-vous est obligatoire")
    private MotifRDV motifRDV;
    
    @Size(max = 500, message = "Les notes sont trop longues (max 500 caractères)")
    private String notes;
}
