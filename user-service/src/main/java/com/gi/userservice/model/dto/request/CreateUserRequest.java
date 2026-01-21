package com.gi.userservice.model.dto.request;

import com.gi.userservice.model.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour la création d'un nouvel utilisateur.
 * 
 * <p>Utilisé par POST /api/users pour créer un compte complet :
 * <ol>
 *   <li>Compte Keycloak (si activé)</li>
 *   <li>Entrée User en PostgreSQL</li>
 *   <li>DoctorProfile ou SecretaryProfile (si role = MEDCIN ou SECRETAIRE)</li>
 * </ol></p>
 * 
 * <p><b>Champs requis :</b></p>
 * <ul>
 *   <li><b>firstName, lastName :</b> Nom complet utilisateur</li>
 *   <li><b>login :</b> Email (sera username Keycloak)</li>
 *   <li><b>password :</b> Mot de passe en clair (hashé côté Keycloak et backend)</li>
 *   <li><b>phoneNumber :</b> Téléphone</li>
 *   <li><b>role :</b> MEDCIN, SECRETAIRE, ADMIN</li>
 *   <li><b>clinicId :</b> REQUIS si role = MEDCIN ou SECRETAIRE (null pour ADMIN)</li>
 * </ul>
 * 
 * <p><b>Exemple JSON :</b></p>
 * <pre>
 * {
 *   "firstName": "Dr. Fatima",
 *   "lastName": "Zahra",
 *   "login": "fatima.zahra@clinic.ma",
 *   "password": "securePass123",
 *   "phoneNumber": "+212661234567",
 *   "role": "MEDCIN",
 *   "clinicId": 3
 * }
 * </pre>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String phoneNumber;
    private UserRole role;
    private Long clinicId;
}