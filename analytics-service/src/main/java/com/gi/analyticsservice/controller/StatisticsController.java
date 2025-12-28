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

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/clinic/{clinicId}/kpis")
    public List<ClinicKpiDTO> getClinicKpis(@PathVariable Long clinicId,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate,
                                            @RequestParam(required = false) PeriodFilter period) {
        StatisticsCriteria criteria = new StatisticsCriteria(clinicId, startDate, endDate, period);
        return statisticsService.getClinicKpis(clinicId, criteria);
    }

    @GetMapping("/global/kpis")
    public List<GlobalKpiDTO> getGlobalKpis(@RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate,
                                            @RequestParam(required = false) PeriodFilter period) {
        StatisticsCriteria criteria = new StatisticsCriteria(null, startDate, endDate, period);
        return statisticsService.getGlobalKpis(criteria);
    }
}