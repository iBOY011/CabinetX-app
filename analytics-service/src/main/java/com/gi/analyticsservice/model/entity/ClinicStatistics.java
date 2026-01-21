package com.gi.analyticsservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entité représentant les statistiques quotidiennes d'un cabinet médical.
 * 
 * <p>Clé composée (ClinicStatisticsId) : (clinicId, date)
 * <ul>
 *   <li>clinicId : identifiant du cabinet</li>
 *   <li>date : date des statistiques (1 ligne par jour et par cabinet)</li>
 * </ul>
 * 
 * <p>Métriques stockées :
 * <ul>
 *   <li>numberOfConsultations : nombre de consultations terminées ce jour</li>
 *   <li>numberOfNewPatients : nombre de nouveaux patients enregistrés</li>
 *   <li>revenue : revenu généré (consultations + actes) en MAD</li>
 * </ul>
 * 
 * <p>Ces statistiques sont calculées automatiquement chaque nuit par un
 * scheduled task qui agrège les données des microservices Consultation,
 * Patient et Billing.
 * 
 * <p>Table : clinic_statistics
 * <p>Index : (clinicId, date) pour recherches efficaces par période
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Entity
@Table(name = "clinic_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicStatistics {
    @EmbeddedId
    private ClinicStatisticsId id;

    @Column(name = "number_of_consultations")
    private int numberOfConsultations;

    @Column(name = "number_of_new_patients")
    private int numberOfNewPatients;

    private BigDecimal revenue;
}