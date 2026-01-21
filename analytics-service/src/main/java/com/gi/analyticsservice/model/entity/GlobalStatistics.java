package com.gi.analyticsservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant les statistiques globales quotidiennes de la plateforme.
 * 
 * <p>Clé primaire : date (1 ligne par jour pour toute la plateforme)
 * 
 * <p>Métriques globales stockées :
 * <ul>
 *   <li>numberOfActiveClinics : nombre de cabinets ayant eu au moins 1 consultation</li>
 *   <li>totalNumberOfUsers : total des utilisateurs inscrits (médecins + secrétaires + patients)</li>
 *   <li>totalNumberOfAppointments : total des rendez-vous créés/traités</li>
 * </ul>
 * 
 * <p>Ces statistiques sont agrégées quotidiennement pour :
 * <ul>
 *   <li>Tableaux de bord administrateurs</li>
 *   <li>Rapports de croissance de la plateforme</li>
 *   <li>Métriques business (taux d'adoption, engagement)</li>
 * </ul>
 * 
 * <p>Calcul automatique par scheduled task chaque nuit en agrégeant :
 * <ul>
 *   <li>ClinicStatistics : compte des cabinets actifs</li>
 *   <li>UserService : compte total des utilisateurs</li>
 *   <li>AppointmentService : compte total des rendez-vous</li>
 * </ul>
 * 
 * <p>Table : global_statistics
 * <p>Index : date (unique) pour accès direct par jour
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Entity
@Table(name = "global_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatistics {
    @Id
    private LocalDate date;

    @Column(name = "number_of_active_clinics")
    private int numberOfActiveClinics;

    @Column(name = "total_number_of_users")
    private int totalNumberOfUsers;

    @Column(name = "total_number_of_appointments")
    private int totalNumberOfAppointments;
}