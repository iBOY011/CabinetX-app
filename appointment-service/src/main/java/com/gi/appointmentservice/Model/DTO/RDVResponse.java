package com.gi.appointmentservice.Model.DTO;
import java.time.LocalDate;

import com.gi.appointmentservice.Model.Enum.StatutRDV;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse contenant les informations complètes d'un rendez-vous.
 * 
 * <p>Hérite de RDVRequest et ajoute les informations suivantes :
 * <ul>
 *   <li>id : identifiant unique du rendez-vous</li>
 *   <li>Informations patient (nom, prénom, CIN, date de naissance)</li>
 *   <li>statutRDV : statut actuel du rendez-vous</li>
 *   <li>queuePosition : position dans la file d'attente (si applicable)</li>
 * </ul>
 * 
 * <p>Ce DTO est enrichi avec les données du patient provenant du microservice Patient
 * via PatientClient. Il évite ainsi les jointures lourdes et suit l'architecture
 * microservices en isolant les domaines.
 * 
 * <p>Utilisé pour :
 * <ul>
 *   <li>Réponses API (GET, POST, PUT endpoints)</li>
 *   <li>Affichage de la file d'attente avec noms des patients</li>
 *   <li>Agenda médical avec informations patient</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class RDVResponse extends RDVRequest{

    private Long id;
    private String prenom;
    private String nom;
    private String cin;
    private LocalDate dateNaissance;
    private StatutRDV statutRDV;
    private Integer queuePosition;
    
}
