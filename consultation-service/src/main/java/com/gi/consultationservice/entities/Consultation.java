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

/**
 * Entité représentant une consultation médicale.
 * 
 * <p>Stockée dans la base de données consultation_db. Une consultation est
 * toujours liée à un rendez-vous (relation 1-1 via rendezVousId unique).
 * 
 * <p>Informations stockées :
 * <ul>
 *   <li>Références : rendezVousId, patientId, medecinId, cabinetId</li>
 *   <li>Médicales : examenClinique, diagnostic, traitement</li>
 *   <li>Complémentaires : examenSupplementaire, observations</li>
 *   <li>Métadonnées : dateConsultation, archived, archivedAt</li>
 * </ul>
 * 
 * <p>Cycle de vie :
 * <ol>
 *   <li>Création : archived = false, champs médicaux vides</li>
 *   <li>En cours : Médecin remplit progressivement les champs</li>
 *   <li>Terminée : archived = true, archivedAt = maintenant</li>
 *   <li>Archivée : Consultation visible dans historique patient</li>
 * </ol>
 * 
 * <p>Types de consultation (ConsultationType) :
 * <ul>
 *   <li>CONSULTATION : Première visite ou nouvelle pathologie</li>
 *   <li>CONTROL : Suivi d'un traitement existant</li>
 * </ul>
 * 
 * <p>Contraintes :
 * <ul>
 *   <li>rendezVousId : UNIQUE (une consultation par RDV)</li>
 *   <li>Champs obligatoires : rendezVousId, patientId, medecinId, cabinetId, type, dateConsultation</li>
 *   <li>Champs texte : limités à 2000 caractères</li>
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
