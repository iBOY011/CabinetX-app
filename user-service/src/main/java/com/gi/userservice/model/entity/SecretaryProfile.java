package com.gi.userservice.model.entity;
    
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Profil complémentaire pour les utilisateurs de rôle SECRETAIRE.
 * 
 * <p>Associe une secrétaire à une clinique spécifique. 
 * <b>Règle métier :</b> Une clinique ne peut avoir qu'UNE SEULE secrétaire active.</p>
 * 
 * <p><b>Champs :</b></p>
 * <ul>
 *   <li><b>id :</b> Identifiant unique du profil</li>
 *   <li><b>userId :</b> Référence vers User.id (NOT NULL, doit pointer vers user avec role=SECRETAIRE)</li>
 *   <li><b>clinicId :</b> Référence vers Clinic.id (NOT NULL, contrainte unicité par clinique)</li>
 * </ul>
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>Récupérer clinicId après login secrétaire (redirection Dashboard)</li>
 *   <li>Valider qu'une secrétaire peut uniquement gérer RDV de SA clinique</li>
 *   <li>Afficher nom secrétaire dans historique modifications RDV (audit)</li>
 * </ul>
 * 
 * <p><b>Table PostgreSQL :</b> secretary_profile</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "secretary_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecretaryProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long clinicId;
}
