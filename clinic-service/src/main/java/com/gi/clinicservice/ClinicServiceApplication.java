package com.gi.clinicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice Clinic.
 * 
 * <p><b>Responsabilités :</b></p>
 * <ul>
 *   <li>Gestion des cabinets médicaux (CRUD)</li>
 *   <li>Activation/désactivation des abonnements</li>
 *   <li>Alertes d'expiration (serviceEndDate)</li>
 * </ul>
 * 
 * <p><b>Base de données :</b> PostgreSQL - clinic_db (port 5435)</p>
 * 
 * <p><b>Port serveur :</b> 8086</p>
 * 
 * <p><b>Eureka :</b> Enregistré comme CLINIC-SERVICE</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class ClinicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicServiceApplication.class, args);
    }

}
