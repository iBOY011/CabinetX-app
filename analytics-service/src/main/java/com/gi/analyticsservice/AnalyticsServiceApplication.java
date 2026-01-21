package com.gi.analyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale du microservice Analytics.
 * 
 * <p>Ce microservice gère la collecte, l'agrégation et la génération de
 * statistiques analytiques pour la plateforme CabinetX :
 * <ul>
 *   <li>KPI par cabinet (consultations, patients, revenus)</li>
 *   <li>KPI globaux (cabinets actifs, utilisateurs, rendez-vous)</li>
 *   <li>Calculs automatiques quotidiens (scheduled tasks)</li>
 *   <li>Rapports de performance et tableaux de bord</li>
 * </ul>
 * 
 * <p>Architecture technique :
 * <ul>
 *   <li>Port : 8086</li>
 *   <li>Base de données : PostgreSQL (analytics_db)</li>
 *   <li>Sécurité : OAuth2/JWT via Keycloak (rôle ADMIN requis)</li>
 *   <li>Découverte : Eureka Service Discovery</li>
 * </ul>
 * 
 * <p>Intégrations :
 * <ul>
 *   <li>ConsultationService : Décompte des consultations</li>
 *   <li>PatientService : Décompte des nouveaux patients</li>
 *   <li>BillingService : Calcul des revenus</li>
 *   <li>AppointmentService : Décompte des rendez-vous</li>
 *   <li>UserService : Décompte des utilisateurs</li>
 * </ul>
 * 
 * <p>Fonctionnalités principales :
 * <ul>
 *   <li>API REST : /api/statistics (consultation KPI)</li>
 *   <li>Scheduled Tasks : Calcul quotidien automatique (01h00)</li>
 *   <li>Agrégation : DAY, WEEK, MONTH</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@SpringBootApplication
public class AnalyticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }

}
