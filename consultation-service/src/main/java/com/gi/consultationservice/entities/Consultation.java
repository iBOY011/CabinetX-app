package com.gi.consultationservice.entities;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.gi.consultationservice.enums.ConsultationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private OffsetDateTime dateConsultation;

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

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean archived = false;

    @Column(name = "archived_at")
    private ZonedDateTime archivedAt;
}
