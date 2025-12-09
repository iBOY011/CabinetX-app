package com.gi.appointmentservice.Model.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import com.gi.appointmentservice.Model.Enum.MotifRDV;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateDto {
    
    private LocalDate date;
    private LocalTime Heure_debut;
    private LocalTime Heure_fin;
    private MotifRDV motifRDV;
    private String notes;
}
