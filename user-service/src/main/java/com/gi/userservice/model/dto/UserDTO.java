package com.gi.userservice.model.dto;

import com.gi.userservice.model.enums.UserRole;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de transfert pour les opérations CRUD sur les utilisateurs.
 * 
 * <p>Utilisé pour :
 * <ul>
 *   <li><b>Requêtes :</b> Création et modification utilisateurs</li>
 *   <li><b>Réponses :</b> Envoi données utilisateur aux clients (inclut clinicId enrichi)</li>
 * </ul></p>
 * 
 * <p><b>Validations :</b></p>
 * <ul>
 *   <li><b>firstName :</b> @NotBlank, @Size(2-50), @Pattern (lettres + accents autorisés)</li>
 *   <li><b>lastName :</b> @NotBlank, @Size(2-50), @Pattern (lettres + accents autorisés)</li>
 *   <li><b>login :</b> @NotBlank, @Email, @Size(max=100)</li>
 *   <li><b>password :</b> @Size(min=6) (envoyé uniquement à la création)</li>
 *   <li><b>phoneNumber :</b> @NotBlank, @Pattern (8-20 caractères, format international)</li>
 *   <li><b>role :</b> @NotNull (MEDCIN, SECRETAIRE, ADMIN)</li>
 * </ul>
 * 
 * <p><b>Champs enrichis (non en base) :</b></p>
 * <ul>
 *   <li><b>clinicId :</b> Chargé depuis DoctorProfile ou SecretaryProfile après requête</li>
 * </ul>
 * 
 * <p><b>Exemple JSON :</b></p>
 * <pre>
 * {
 *   "id": 1,
 *   "keycloakUserId": "abc-123-def",
 *   "firstName": "Dr. Ahmed",
 *   "lastName": "Majidi",
 *   "login": "ahmed.majidi@example.com",
 *   "phoneNumber": "+212612345678",
 *   "role": "MEDCIN",
 *   "clinicId": 5,
 *   "active": true
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
public class UserDTO {

    private Long id;
    private String keycloakUserId;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom contient des caractères invalides")
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom contient des caractères invalides")
    private String lastName;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email est trop long (max 100 caractères)")
    private String login;
    
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^[0-9+\\s\\-()]{8,20}$", message = "Format de téléphone invalide")
    private String phoneNumber;
    
    @NotNull(message = "Le rôle est obligatoire")
    private UserRole role;
    
    private Long clinicId;
    private boolean active;
}