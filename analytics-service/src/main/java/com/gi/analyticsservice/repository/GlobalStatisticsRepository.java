package com.gi.analyticsservice.repository;

import com.gi.analyticsservice.model.entity.GlobalStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion de la persistence des statistiques globales.
 * 
 * <p>Fournit des méthodes de requêtes pour récupérer les statistiques
 * quotidiennes globales de la plateforme sur une période donnée.
 * 
 * <p>Méthode principale :
 * <ul>
 *   <li>findByDateBetween : Recherche par intervalle de dates</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public interface GlobalStatisticsRepository extends JpaRepository<GlobalStatistics, LocalDate> {
    /**
     * Récupère les statistiques globales pour un intervalle de dates.
     * 
     * @param start date de début (inclusive)
     * @param end date de fin (inclusive)
     * @return liste ordonnée des statistiques quotidiennes
     */
    List<GlobalStatistics> findByDateBetween(LocalDate start, LocalDate end);
}