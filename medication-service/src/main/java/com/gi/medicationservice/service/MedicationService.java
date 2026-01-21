package com.gi.medicationservice.service;

import com.gi.medicationservice.model.dto.MedicationDTO;

import java.util.List;

/**
 * Service de gestion du catalogue de médicaments.
 * 
 * <p>Gère le référentiel de médicaments utilisé dans les ordonnances.
 * Fournit recherche autocomplete pour faciliter la saisie des médecins.</p>
 * 
 * <p><b>Opérations :</b></p>
 * <ul>
 *   <li>CRUD médicaments (création, modification, suppression)</li>
 *   <li>Recherche autocomplete par nom commercial OU DCI (case-insensitive)</li>
 *   <li>Import en masse depuis fichiers CSV/Excel/JSON</li>
 * </ul>
 * 
 * <p><b>Cas d'usage autocomplete :</b></p>
 * <ul>
 *   <li>Médecin saisit "amo" → Retourne Amoxicilline, Amoxil, etc.</li>
 *   <li>Médecin saisit "500" → Retourne tous médicaments avec dosage 500mg</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public interface MedicationService {

    MedicationDTO createMedication(MedicationDTO dto);

    MedicationDTO updateMedication(Long id, MedicationDTO dto);

    void deleteMedication(Long id);

    MedicationDTO findById(Long id);

    List<MedicationDTO> autocomplete(String term);

    List<MedicationDTO> findAll();

    int importFromFile(byte[] file);
}