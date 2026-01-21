package com.gi.patientservice;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.gi.patientservice.entities.Patient;
import com.gi.patientservice.repository.PatientRepository;

/**
 * Classe principale du microservice Patient.
 * 
 * <p>Ce microservice gère le cycle de vie complet des patients dans le système CabinetX :
 * <ul>
 *   <li>CRUD complet des données patient</li>
 *   <li>Validation des données (CIN, téléphone marocains)</li>
 *   <li>Recherche avancée (nom, prénom, CIN)</li>
 *   <li>Audit trail des modifications</li>
 *   <li>Gestion des adresses et couvertures médicales</li>
 * </ul>
 * 
 * <p>Architecture :
 * <ul>
 *   <li>Port : 8081</li>
 *   <li>Base de données : PostgreSQL (patient_db)</li>
 *   <li>Sécurité : Authentification JWT via Gateway</li>
 *   <li>Découverte : Eureka Service Discovery</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@SpringBootApplication
public class PatientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    /**
     * Initialise des données de test au démarrage (commenté par défaut).
     * 
     * <p>Ce bean CommandLineRunner peut être décommenté pour peupler la base
     * avec des patients de test représentatifs (noms marocains, formats locaux).
     * 
     * @param patientRepository le repository pour sauvegarder les patients
     * @return le CommandLineRunner exécuté au démarrage
     */
    @Bean
    CommandLineRunner start(PatientRepository patientRepository) {
        return args -> {
            List<Patient> samples = List.of(
                /* Patient.builder()
                    .cin("AB123456")
                    .nom("Alami")
                    .prenom("Mohamed")
                    .dateNaissance(LocalDate.of(1985, 3, 15))
                    .sexe(Sexe.MASCULIN)
                    .numTel("0612345678")
                    .typeMutuelle(TypeMutuelle.CNSS)
                    .cabinetId(1L)
                    .adresse(Adresse.builder()
                        .rue("12 Rue des Atlas")
                        .ville("Casablanca")
                        .codePostal("20000")
                        .pays("Maroc")
                        .build())
                    .build(),
    
                Patient.builder()
                    .cin("CD789012")
                    .nom("Bennani")
                    .prenom("Fatima")
                    .dateNaissance(LocalDate.of(1990, 7, 22))
                    .sexe(Sexe.FEMININ)
                    .numTel("0623456789")
                    .typeMutuelle(TypeMutuelle.CNOPS)
                    .cabinetId(1L)
                    .adresse(Adresse.builder()
                        .rue("5 Avenue Mohammed V")
                        .ville("Rabat")
                        .codePostal("10000")
                        .pays("Maroc")
                        .build())
                    .build(),
    
                Patient.builder()
                    .cin("EF345678")
                    .nom("Idrissi")
                    .prenom("Karim")
                    .dateNaissance(LocalDate.of(1978, 11, 8))
                    .sexe(Sexe.MASCULIN)
                    .numTel("0634567890")
                    .typeMutuelle(TypeMutuelle.PRIVEE)
                    .cabinetId(2L)
                    .adresse(Adresse.builder()
                        .rue("23 Boulevard Hassan II")
                        .ville("Marrakech")
                        .codePostal("40000")
                        .pays("Maroc")
                        .build())
                    .build(),
    
                Patient.builder()
                    .cin("GH901234")
                    .nom("Et-tayeb")
                    .prenom("Salma")
                    .dateNaissance(LocalDate.of(1995, 5, 4))
                    .sexe(Sexe.FEMININ)
                    .numTel("0645678901")
                    .typeMutuelle(TypeMutuelle.AUCUNE)
                    .cabinetId(2L)
                    .adresse(Adresse.builder()
                        .rue("8 Rue de Fès")
                        .ville("Fès")
                        .codePostal("30000")
                        .pays("Maroc")
                        .build())
                    .build() */
            );

            patientRepository.saveAll(samples);
        };
    }
}
