package com.gi.medicationservice.repository;

import com.gi.medicationservice.model.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité Medication.
 * 
 * <p>Fournit les méthodes de requête pour accéder au catalogue de médicaments dans PostgreSQL.
 * Inclut recherche par nom commercial et DCI (case-insensitive, recherche partielle).</p>
 * 
 * <p><b>Méthodes personnalisées :</b></p>
 * <ul>
 *   <li><b>findByCommercialNameContainingIgnoreCase :</b> Recherche par nom commercial (LIKE %term% ILIKE)</li>
 *   <li><b>findByDciContainingIgnoreCase :</b> Recherche par DCI / principe actif</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /**
     * Trouve tous les médicaments dont le nom commercial contient le terme de recherche.
     * 
     * @param term Le terme de recherche (case-insensitive)
     * @return Liste des médicaments correspondants
     * 
     * <p><b>Cas d'usage :</b> Autocomplete frontend - saisie "para" → Paracétamol, Paraffine, etc.</p>
     */
    List<Medication> findByCommercialNameContainingIgnoreCase(String term);

    /**
     * Trouve tous les médicaments dont la DCI contient le terme de recherche.
     * 
     * @param term Le terme de recherche (case-insensitive)
     * @return Liste des médicaments correspondants
     * 
     * <p><b>Cas d'usage :</b> Recherche par principe actif - "amoxicilline" → Tous médicaments à base d'amoxicilline</p>
     */
    List<Medication> findByDciContainingIgnoreCase(String term);
}