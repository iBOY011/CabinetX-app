package com.gi.analyticsservice.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Clé composée pour l'entité ClinicStatistics.
 * 
 * <p>Identifie de manière unique les statistiques d'un cabinet pour une date donnée.
 * Implémente Serializable pour permettre l'utilisation comme clé primaire JPA.
 * 
 * <p>Composition :
 * <ul>
 *   <li>clinicId : identifiant du cabinet</li>
 *   <li>date : date des statistiques</li>
 * </ul>
 * 
 * <p>Cette clé composée garantit qu'il ne peut y avoir qu'une seule ligne de
 * statistiques par cabinet et par jour.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicStatisticsId implements Serializable {
    private Long clinicId;
    private LocalDate date;
}