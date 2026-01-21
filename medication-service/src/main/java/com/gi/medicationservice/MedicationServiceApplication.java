package com.gi.medicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice Medication.
 * 
 * <p><b>Responsabilités :</b></p>
 * <ul>
 *   <li>Gestion du catalogue de médicaments (CRUD)</li>
 *   <li>Recherche autocomplete pour ordonnances (nom commercial + DCI)</li>
 *   <li>Import en masse (CSV/Excel/JSON)</li>
 * </ul>
 * 
 * <p><b>Base de données :</b> PostgreSQL - medication_db (port 5439)</p>
 * 
 * <p><b>Port serveur :</b> 8090</p>
 * 
 * <p><b>Eureka :</b> Enregistré comme MEDICATION-SERVICE</p>
 * 
 * <p><b>Consommé par :</b></p>
 * <ul>
 *   <li>Prescription Service - Création ordonnances</li>
 *   <li>Frontend - Autocomplete médicaments lors saisie ordonnance</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class MedicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicationServiceApplication.class, args);
    }

}
