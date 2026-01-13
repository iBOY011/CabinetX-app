package com.gi.appointmentservice.Model.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crenaux {
    private Long id;
    private Long cabinetId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Boolean disponible;
}
