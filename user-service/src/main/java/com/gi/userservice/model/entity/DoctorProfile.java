package com.gi.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Profil complémentaire pour les utilisateurs de rôle MEDECIN.
 * 
 * <p>Associe un médecin à une clinique spécifique. 
 * <b>Règle métier :</b> Une clinique ne peut avoir qu'UN SEUL médecin actif.</p>
 * 
 * <p><b>Champs :</b></p>
 * <ul>
 *   <li><b>id :</b> Identifiant unique du profil</li>
 *   <li><b>userId :</b> Référence vers User.id (NOT NULL, doit pointer vers user avec role=MEDCIN)</li>
 *   <li><b>clinicId :</b> Référence vers Clinic.id (NOT NULL, contrainte unicité par clinique)</li>
 *   <li><b>digitalSignature :</b> Signature numérique base64 du médecin (pour ordonnances PDF)</li>
 * </ul>
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>Récupérer clinicId après login médecin (redirection Dashboard)</li>
 *   <li>Afficher signature sur ordonnances PDF</li>
 *   <li>Valider qu'un médecin peut uniquement voir patients de SA clinique</li>
 * </ul>
 * 
 * <p><b>Table PostgreSQL :</b> doctor_profile</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "doctor_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String digitalSignature;

    @Column(nullable = false)
    private Long clinicId;

}