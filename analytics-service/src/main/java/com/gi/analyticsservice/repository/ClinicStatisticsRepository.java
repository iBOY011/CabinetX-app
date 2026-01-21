package com.gi.analyticsservice.repository;

import com.gi.analyticsservice.model.entity.ClinicStatistics;
import com.gi.analyticsservice.model.entity.ClinicStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion de la persistence des statistiques de cabinet.
 * 
 * <p>Fournit des méthodes de requêtes pour récupérer les statistiques
 * quotidiennes d'un cabinet sur une période donnée.
 * 
 * <p>Méthode principale :
 * <ul>
 *   <li>findByIdClinicIdAndIdDateBetween : Recherche par cabinet et intervalle de dates</li>
 * </ul>
 * 
 * <p>Cette requête utilise l'@EmbeddedId pour filtrer efficacement sur
 * la clé composée (clinicId, date).
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public interface ClinicStatisticsRepository extends JpaRepository<ClinicStatistics, ClinicStatisticsId> {
    /**
     * Récupère les statistiques d'un cabinet pour un intervalle de dates.
     * 
     * @param clinicId identifiant du cabinet
     * @param start date de début (inclusive)
     * @param end date de fin (inclusive)
     * @return liste ordonnée des statistiques quotidiennes
     */
    List<ClinicStatistics> findByIdClinicIdAndIdDateBetween(Long clinicId, LocalDate start, LocalDate end);
}