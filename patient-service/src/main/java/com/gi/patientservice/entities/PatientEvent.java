package com.gi.patientservice.entities;

import com.gi.patientservice.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant un événement dans l'historique d'un patient.
 * 
 * <p>Cette entité permet de tracker tous les changements effectués sur un patient
 * (création, modification, suppression) pour des besoins d'audit et de traçabilité.
 * 
 * <p>Chaque événement enregistre automatiquement sa date/heure de création via
 * le hook @PrePersist. La relation ManyToOne vers Patient utilise LAZY loading
 * pour optimiser les performances.
 * 
 * <p>Utilisation typique :
 * <pre>
 * PatientEvent event = PatientEvent.builder()
 *     .patientId(patient.getId())
 *     .type(EventType.UPDATED)
 *     .build();
 * // occurredAt est automatiquement renseigné
 * </pre>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "patient")
public class PatientEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @PrePersist
    void onPersist() {
        occurredAt = LocalDateTime.now();
    }
}
