package com.gi.appointmentservice.Model.DTO;
import java.time.LocalDate;
import java.time.LocalTime;


public class Crenaux {
    
    Long id;
    Long cabinetId;
    LocalDate date;
    LocalTime Heure_debut;
    LocalTime Heure_fin;
    Boolean disponible;
}
