package com.gi.userservice.model.enums;

/**
 * Rôles des utilisateurs dans l'application CabinetX.
 * 
 * <p><b>Valeurs possibles :</b></p>
 * <ul>
 *   <li><b>ADMIN :</b> Administrateur système (accès complet, gestion toutes cliniques)</li>
 *   <li><b>MEDCIN :</b> Médecin (accès consultations, ordonnances, patients de SA clinique uniquement)</li>
 *   <li><b>SECRETAIRE :</b> Secrétaire médicale (gestion RDV, file d'attente, patients de SA clinique uniquement)</li>
 * </ul>
 * 
 * <p><b>Associations :</b></p>
 * <ul>
 *   <li>ADMIN : Pas de profil, pas de clinicId</li>
 *   <li>MEDCIN : Requiert DoctorProfile (userId, clinicId, digitalSignature)</li>
 *   <li>SECRETAIRE : Requiert SecretaryProfile (userId, clinicId)</li>
 * </ul>
 * 
 * <p><b>Permissions Spring Security :</b></p>
 * <ul>
 *   <li>ADMIN : Tous endpoints</li>
 *   <li>MEDCIN : hasRole('MEDCIN') + @PreAuthorize("#clinicId == principal.clinicId")</li>
 *   <li>SECRETAIRE : hasRole('SECRETAIRE') + @PreAuthorize("#clinicId == principal.clinicId")</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public enum UserRole {
    ADMIN,
    MEDCIN,
    SECRETAIRE
}