package com.gi.patientservice.entities;

import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a patient in the medical practice management system.
 * 
 * <p>This entity stores complete patient demographics and contact information
 * following Moroccan healthcare standards. Key features:</p>
 * <ul>
 *   <li>CIN (National Identity Card) as unique identifier</li>
 *   <li>Support for Moroccan healthcare insurance types (CNSS, CNOPS, etc.)</li>
 *   <li>Embedded address structure for flexible address management</li>
 *   <li>Association with clinic via cabinetId</li>
 *   <li>Automatic timestamp management via JPA callbacks</li>
 * </ul>
 * 
 * <p><b>Database Constraints:</b></p>
 * <ul>
 *   <li>CIN must be unique (database level constraint)</li>
 *   <li>All required fields enforced at DB level (NOT NULL)</li>
 *   <li>Foreign key relationship with Clinic entity</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 * @see Adresse
 * @see Sexe
 * @see TypeMutuelle
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cin;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexe sexe;

    @Column(nullable = false)
    private String numTel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMutuelle typeMutuelle;

    @Embedded
    private Adresse adresse;

    @Column(nullable = false)
    private Long cabinetId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
