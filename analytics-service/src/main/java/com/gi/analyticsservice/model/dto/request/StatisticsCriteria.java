package com.gi.analyticsservice.model.dto.request;

import com.gi.analyticsservice.model.enums.PeriodFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de critères de filtrage pour les requêtes de statistiques.
 * 
 * <p>Permet de spécifier les paramètres de recherche pour les KPI :
 * <ul>
 *   <li>clinicId : filtre par cabinet (null pour statistiques globales)</li>
 *   <li>startDate : date de début de la période</li>
 *   <li>endDate : date de fin de la période</li>
 *   <li>period : granularité d'agrégation (DAY, WEEK, MONTH)</li>
 * </ul>
 * 
 * <p>Valeurs par défaut (si non spécifiées) :
 * <ul>
 *   <li>startDate : aujourd'hui - 30 jours</li>
 *   <li>endDate : aujourd'hui</li>
 *   <li>period : DAY</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsCriteria {
    private Long clinicId;
    private LocalDate startDate;
    private LocalDate endDate;
    private PeriodFilter period;
}