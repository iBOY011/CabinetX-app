package com.gi.clinicservice.repository;

import com.gi.clinicservice.model.entity.Clinic;
import com.gi.clinicservice.model.enums.ClinicStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité Clinic.
 * 
 * <p>Fournit les méthodes de requête pour accéder aux cabinets médicaux dans PostgreSQL.
 * Inclut des requêtes personnalisées pour filtrer par statut et date d'expiration.</p>
 * 
 * <p><b>Méthodes personnalisées :</b></p>
 * <ul>
 *   <li><b>findByStatus :</b> Récupère cabinets ACTIVE ou INACTIVE (pour liste admin)</li>
 *   <li><b>findByServiceEndDateBefore :</b> Alerte cabinets expirant avant une date</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    
    /**
     * Trouve tous les cabinets ayant un statut spécifique.
     * 
     * @param status Le statut recherché (ACTIVE ou INACTIVE)
     * @return Liste des cabinets correspondants
     * 
     * <p><b>Cas d'usage :</b> Afficher uniquement cabinets actifs dans sélecteur RDV</p>
     */
    List<Clinic> findByStatus(ClinicStatus status);
    
    /**
     * Trouve tous les cabinets dont la date de fin de service est avant la date spécifiée.
     * 
     * @param date La date limite de recherche
     * @return Liste des cabinets expirant avant cette date
     * 
     * <p><b>Cas d'usage :</b> Envoyer alerte email aux administrateurs 30 jours avant expiration :
     * <code>findByServiceEndDateBefore(LocalDate.now().plusDays(30))</code></p>
     */
    List<Clinic> findByServiceEndDateBefore(LocalDate date);
}