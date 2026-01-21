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

/**
 * Entité représentant un rendez-vous médical.
 * 
 * <p>Stocké dans la base de données appointment_db. Cette entité contient toutes
 * les informations nécessaires à la gestion des rendez-vous :
 * <ul>
 *   <li>Informations temporelles (date, heure début/fin)</li>
 *   <li>Références aux entités externes (patientId, cabinetId)</li>
 *   <li>Informations médicales (motif, notes)</li>
 *   <li>Gestion du statut (CONFIRMÉ, EN_ATTENTE, EN_CONSULTATION, etc.)</li>
 *   <li>Position dans la file d'attente (queuePosition)</li>
 * </ul>
 * 
 * <p>Cycle de vie typique d'un rendez-vous :
 * <ol>
 *   <li>Création : statut = CONFIRMÉ</li>
 *   <li>Arrivée du patient : statut = EN_ATTENTE, queuePosition assignée</li>
 *   <li>Appel du patient : statut = EN_CONSULTATION</li>
 *   <li>Fin de consultation : statut = TERMINÉ</li>
 * </ol>
 * 
 * <p>La propriété queuePosition est nullable et utilisée uniquement lorsque
 * le rendez-vous est EN_ATTENTE dans la file virtuelle.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
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

    @Column
    private Integer queuePosition;

}
