package com.gi.analyticsservice.model.enums;

/**
 * Énumération des périodes d'agrégation pour les statistiques.
 * 
 * <p>Permet de grouper les statistiques quotidiennes par :
 * <ul>
 *   <li>DAY : Données quotidiennes (pas d'agrégation)</li>
 *   <li>WEEK : Agrégation par semaine (somme/moyenne hebdomadaire)</li>
 *   <li>MONTH : Agrégation par mois (somme/moyenne mensuelle)</li>
 * </ul>
 * 
 * <p>Utilisé dans StatisticsCriteria pour contrôler la granularité
 * des rapports KPI retournés par l'API.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public enum PeriodFilter {
    /** Statistiques quotidiennes (pas d'agrégation) */
    DAY,
    
    /** Agrégation hebdomadaire */
    WEEK,
    
    /** Agrégation mensuelle */
    MONTH
}