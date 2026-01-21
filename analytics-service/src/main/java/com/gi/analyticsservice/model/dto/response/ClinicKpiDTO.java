package com.gi.analyticsservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de réponse contenant les KPI d'un cabinet pour une date donnée.
 * 
 * <p>Représente les métriques de performance quotidiennes d'un cabinet :
 * <ul>
 *   <li>date : date des statistiques</li>
 *   <li>numberOfConsultations : nombre de consultations terminées</li>
 *   <li>numberOfNewPatients : nouveaux patients enregistrés</li>
 *   <li>revenue : revenu généré en MAD (consultations + actes)</li>
 * </ul>
 * 
 * <p>Utilisé pour :
 * <ul>
 *   <li>Tableaux de bord médecins : graphiques de performance</li>
 *   <li>Rapports mensuels : calcul de revenus et activité</li>
 *   <li>Comparaisons temporelles : évolution des KPI</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicKpiDTO {
    private LocalDate date;
    private int numberOfConsultations;
    private int numberOfNewPatients;
    private BigDecimal revenue;
}