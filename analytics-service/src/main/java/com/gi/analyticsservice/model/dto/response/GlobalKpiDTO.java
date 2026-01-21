package com.gi.analyticsservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de réponse contenant les KPI globaux de la plateforme pour une date donnée.
 * 
 * <p>Représente les métriques d'activité globale de la plateforme CabinetX :
 * <ul>
 *   <li>date : date des statistiques</li>
 *   <li>numberOfActiveClinics : cabinets ayant eu au moins 1 consultation</li>
 *   <li>totalNumberOfUsers : total utilisateurs (médecins + secrétaires + patients)</li>
 *   <li>totalNumberOfAppointments : total rendez-vous créés/traités</li>
 * </ul>
 * 
 * <p>Utilisé pour :
 * <ul>
 *   <li>Tableaux de bord administrateurs : vue d'ensemble plateforme</li>
 *   <li>Rapports business : taux de croissance, engagement</li>
 *   <li>Métriques stratégiques : adoption, utilisation</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalKpiDTO {
    private LocalDate date;
    private int numberOfActiveClinics;
    private int totalNumberOfUsers;
    private int totalNumberOfAppointments;
}