package com.gi.clinicservice.model.enums;

/**
 * Statut d'un cabinet médical dans l'application CabinetX.
 * 
 * <p><b>Valeurs possibles :</b></p>
 * <ul>
 *   <li><b>ACTIVE :</b> Cabinet avec abonnement valide, peut créer RDV et consultations</li>
 *   <li><b>INACTIVE :</b> Cabinet avec abonnement expiré, accès limité en lecture seule</li>
 * </ul>
 * 
 * <p><b>Transitions :</b></p>
 * <ul>
 *   <li>ACTIVE → INACTIVE : Expiration serviceEndDate ou désactivation manuelle PATCH /api/clinics/{id}/deactivate</li>
 *   <li>INACTIVE → ACTIVE : Renouvellement abonnement via PATCH /api/clinics/{id}/activate</li>
 * </ul>
 * 
 * <p><b>Impact métier :</b></p>
 * <ul>
 *   <li>Cabinet INACTIVE : Médecins ne peuvent plus créer RDV, patients existants conservés</li>
 *   <li>Cabinet ACTIVE : Accès complet à toutes fonctionnalités</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public enum ClinicStatus {
    ACTIVE,
    INACTIVE
}