package com.gi.appointmentservice.Model.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplifié contenant les informations essentielles d'un patient.
 * 
 * <p>Utilisé pour enrichir les RDVResponse avec les données patient sans
 * exposer toutes les informations sensibles (adresse, téléphone, mutuelle, etc.).
 * 
 * <p>Champs inclus :
 * <ul>
 *   <li>id : identifiant unique</li>
 *   <li>prenom, nom : identité</li>
 *   <li>cin : carte d'identité nationale marocaine</li>
 *   <li>dateNaissance : pour calcul d'âge et identification</li>
 * </ul>
 * 
 * <p>Ce DTO est reçu depuis le microservice Patient via PatientClient.
 * Il représente un sous-ensemble de PatientDTO complet.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PatientInfoDTO {
    Long id;
    String prenom;
    String nom;
    String cin;
    LocalDate dateNaissance;

}
