package com.gi.medicalrecordservice.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant le dossier médical d'un patient.
 * 
 * <p>Un dossier médical est unique par patient (patientId unique) et contient :
 * <ul>
 *   <li>Antécédents médicaux (medicalHistory) : Maladies passées, chirurgies</li>
 *   <li>Allergies (allergies) : Médicamenteuses, alimentaires, autres</li>
 *   <li>Traitements en cours (treatments) : Médicaments actuels</li>
 *   <li>Habitudes de vie (habits) : Tabac, alcool, activité physique</li>
 *   <li>Documents attachés (documents) : Radiographies, analyses, etc.</li>
 * </ul>
 * 
 * <p>Cycle de vie :
 * <ol>
 *   <li>Création : Automatique lors de la première consultation</li>
 *   <li>Mise à jour : Continue par les médecins lors des consultations</li>
 *   <li>Enrichissement : Documents ajoutés au fil du temps</li>
 * </ol>
 * 
 * <p>Relation OneToMany avec MedicalDocument :
 * <ul>
 *   <li>Cascade ALL : Suppression dossier = suppression documents</li>
 *   <li>orphanRemoval : Documents orphelins automatiquement supprimés</li>
 *   <li>FetchType LAZY : Documents chargés à la demande</li>
 * </ul>
 * 
 * <p>Hooks JPA :
 * <ul>
 *   <li>@PrePersist : Initialise creationDate et lastUpdate</li>
 *   <li>@PreUpdate : Met à jour lastUpdate automatiquement</li>
 * </ul>
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
@ToString(exclude = "documents")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long patientId;

    private String medicalHistory;
    private String allergies;
    private String treatments;
    private String habits;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    @Builder.Default
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedicalDocument> documents = new ArrayList<>();

    @PrePersist
    void prePersist() {
        creationDate = LocalDateTime.now();
        lastUpdate = creationDate;
    }

    @PreUpdate
    void preUpdate() {
        lastUpdate = LocalDateTime.now();
    }

    public void addDocument(MedicalDocument document) {
        document.setMedicalRecord(this);
        documents.add(document);
    }
}