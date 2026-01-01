package com.gi.appointmentservice.Model.Entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.gi.appointmentservice.Model.Enum.MotifRDV;
import com.gi.appointmentservice.Model.Enum.StatutRDV;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime Heure_debut;

    @Column(nullable = false)
    private LocalTime Heure_fin;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotifRDV motifRDV;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRDV statutRDV;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long cabinetId;

    private String notes;

}
