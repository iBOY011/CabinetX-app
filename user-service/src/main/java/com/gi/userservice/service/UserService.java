package com.gi.userservice.service;

import java.util.List;

import com.gi.userservice.model.dto.UserDTO;
import com.gi.userservice.model.dto.request.CreateUserRequest;
import com.gi.userservice.model.enums.UserRole;

/**
 * Service de gestion des utilisateurs avec intégration Keycloak.
 * 
 * <p>Gère les utilisateurs de l'application CabinetX (médecins, secrétaires, admins) 
 * avec synchronisation Keycloak pour l'authentification SSO.</p>
 * 
 * <p><b>Opérations :</b></p>
 * <ul>
 *   <li>Création utilisateur : PostgreSQL + Keycloak + DoctorProfile/SecretaryProfile</li>
 *   <li>Activation/Désactivation : Toggle active (contrôle accès application)</li>
 *   <li>Recherche par clinique/rôle/login</li>
 * </ul>
 * 
 * <p><b>Règles métier critiques :</b></p>
 * <ul>
 *   <li>1 clinique = 1 médecin + 1 secrétaire maximum</li>
 *   <li>MEDECIN requiert DoctorProfile (userId, clinicId, digitalSignature)</li>
 *   <li>SECRETAIRE requiert SecretaryProfile (userId, clinicId)</li>
 *   <li>ADMIN n'a pas de profil (pas lié à une clinique)</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public interface UserService {

    UserDTO createUser(CreateUserRequest request);

    UserDTO findById(Long id);

    List<UserDTO> findAll();

    UserDTO updateUser(Long id, UserDTO dto);

    UserDTO activateUser(Long id);

    UserDTO deactivateUser(Long id);

    List<UserDTO> listByClinic(Long clinicId);

    List<UserDTO> listByRole(UserRole role);

    UserDTO findByLogin(String login);

    List<UserDTO> findByCabinetIdAndRole(Long cabinetId, String role);
}