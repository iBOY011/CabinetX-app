package com.gi.analyticsservice.controller;

import com.gi.analyticsservice.model.dto.request.StatisticsCriteria;
import com.gi.analyticsservice.model.dto.response.ClinicKpiDTO;
import com.gi.analyticsservice.model.dto.response.GlobalKpiDTO;
import com.gi.analyticsservice.model.enums.PeriodFilter;
import com.gi.analyticsservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur REST pour la consultation des statistiques et KPI analytiques.
 * 
 * <p>Expose les endpoints suivants :
 * <ul>
 *   <li>GET /api/statistics/clinic/{id}/kpis : KPI d'un cabinet spécifique</li>
 *   <li>GET /api/statistics/global/kpis : KPI globaux de la plateforme</li>
 * </ul>
 * 
 * <p>Paramètres de filtrage communs :
 * <ul>
 *   <li>startDate : date de début (optionnel, défaut = -30 jours)</li>
 *   <li>endDate : date de fin (optionnel, défaut = aujourd'hui)</li>
 *   <li>period : période d'agrégation (DAY, WEEK, MONTH)</li>
 * </ul>
 * 
 * <p>Exemples d'utilisation :
 * <pre>
 * // KPI du cabinet 1 pour les 30 derniers jours
 * GET /api/statistics/clinic/1/kpis
 * 
 * // KPI du cabinet 1 pour janvier 2026 groupés par semaine
 * GET /api/statistics/clinic/1/kpis?startDate=2026-01-01&endDate=2026-01-31&period=WEEK
 * 
 * // KPI globaux pour l'année 2025 groupés par mois
 * GET /api/statistics/global/kpis?startDate=2025-01-01&endDate=2025-12-31&period=MONTH
 * </pre>
 * 
 * <p>Sécurité :
 * <ul>
 *   <li>KPI cabinet : MEDECIN, SECRETAIRE (propre cabinet uniquement)</li>
 *   <li>KPI global : ADMIN uniquement</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    /**
     * Récupère les KPI d'un cabinet pour une période donnée.
     * 
     * @param clinicId identifiant du cabinet
     * @param startDate date de début (optionnel)
     * @param endDate date de fin (optionnel)
     * @param period période d'agrégation (DAY, WEEK, MONTH)
     * @return liste des KPI quotidiens avec consultations, patients, revenus
     */
    @GetMapping("/clinic/{clinicId}/kpis")
    public List<ClinicKpiDTO> getClinicKpis(@PathVariable Long clinicId,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate,
                                            @RequestParam(required = false) PeriodFilter period) {
        StatisticsCriteria criteria = new StatisticsCriteria(clinicId, startDate, endDate, period);
        return statisticsService.getClinicKpis(clinicId, criteria);
    }

    /**
     * Récupère les KPI globaux de la plateforme pour une période donnée.
     * 
     * @param startDate date de début (optionnel)
     * @param endDate date de fin (optionnel)
     * @param period période d'agrégation (DAY, WEEK, MONTH)
     * @return liste des KPI globaux avec cabinets actifs, utilisateurs, rendez-vous
     */
    @GetMapping("/global/kpis")
    public List<GlobalKpiDTO> getGlobalKpis(@RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate,
                                            @RequestParam(required = false) PeriodFilter period) {
        StatisticsCriteria criteria = new StatisticsCriteria(null, startDate, endDate, period);
        return statisticsService.getGlobalKpis(criteria);
    }
}