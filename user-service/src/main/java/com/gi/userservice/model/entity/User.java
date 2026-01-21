package com.gi.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.gi.userservice.model.enums.UserRole;

/**
 * Entité JPA représentant un utilisateur de l'application CabinetX.
 * 
 * <p>Table centrale pour tous les utilisateurs (médecins, secrétaires, admins). 
 * Synchronisée avec Keycloak via keycloakUserId pour SSO.</p>
 * 
 * <p><b>Champs clés :</b></p>
 * <ul>
 *   <li><b>id :</b> Identifiant unique PostgreSQL</li>
 *   <li><b>keycloakUserId :</b> UUID Keycloak (unique, nullable si Keycloak désactivé en dev)</li>
 *   <li><b>firstName, lastName :</b> Nom complet utilisateur</li>
 *   <li><b>login :</b> Email unique (utilisé comme username Keycloak)</li>
 *   <li><b>password :</b> Mot de passe hashé (synchronisé avec Keycloak)</li>
 *   <li><b>phoneNumber :</b> Téléphone (format international recommandé)</li>
 *   <li><b>role :</b> MEDCIN, SECRETAIRE, ADMIN (enum UserRole)</li>
 *   <li><b>active :</b> true = compte actif, false = compte désactivé (interdit connexion)</li>
 * </ul>
 * 
 * <p><b>Relations :</b></p>
 * <ul>
 *   <li>1 User (role=MEDCIN) → 1 DoctorProfile (userId, clinicId, digitalSignature)</li>
 *   <li>1 User (role=SECRETAIRE) → 1 SecretaryProfile (userId, clinicId)</li>
 *   <li>1 User (role=ADMIN) → Aucun profil</li>
 * </ul>
 * 
 * <p><b>Table PostgreSQL :</b> users</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class    User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_user_id", unique = true)
    private String keycloakUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean active = true;
}