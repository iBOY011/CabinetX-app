package com.gi.clinicservice.service;

import com.gi.clinicservice.model.dto.ClinicDTO;

import java.util.List;

/**
 * Service de gestion des cabinets médicaux.
 * 
 * <p>Définit les opérations CRUD et métier pour les cabinets, incluant :
 * <ul>
 *   <li>Création et modification de cabinets</li>
 *   <li>Activation/désactivation de cabinets (gestion des abonnements)</li>
 *   <li>Recherche de cabinets actifs ou proches de l'expiration</li>
 * </ul></p>
 * 
 * <p><b>Règles métier :</b></p>
 * <ul>
 *   <li>Un cabinet INACTIVE ne peut pas créer de rendez-vous</li>
 *   <li>serviceEndDate doit être dans le futur lors de la création</li>
 *   <li>Le système envoie des alertes 30 jours avant l'expiration</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public interface ClinicService {
    ClinicDTO createClinic(ClinicDTO request);
    ClinicDTO updateClinic(Long id, ClinicDTO request);
    ClinicDTO activateClinic(Long id);
    ClinicDTO deactivateClinic(Long id);
    ClinicDTO findById(Long id);
    List<ClinicDTO> findAll();
    List<ClinicDTO> findActive();
    List<ClinicDTO> findNearExpiration(int daysBefore);
}