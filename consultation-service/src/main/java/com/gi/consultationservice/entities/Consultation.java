package com.gi.consultationservice.entities;

import com.gi.consultationservice.enums.ConsultationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long rendezVousId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long medecinId;

    @Column(nullable = false)
    private Long cabinetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultationType type;

    @Column(nullable = false)
    private LocalDateTime dateConsultation;

    @Column(length = 2000)
    private String examenClinique;

    @Column(length = 2000)
    private String examenSupplementaire;

    @Column(length = 2000)
    private String diagnostic;

    @Column(length = 2000)
    private String traitement;

    @Column(length = 2000)
    private String observations;
}
