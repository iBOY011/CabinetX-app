package com.gi.analyticsservice.service;

import com.gi.analyticsservice.model.dto.request.StatisticsCriteria;
import com.gi.analyticsservice.model.dto.response.ClinicKpiDTO;
import com.gi.analyticsservice.model.dto.response.GlobalKpiDTO;
import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    List<ClinicKpiDTO> getClinicKpis(Long clinicId, StatisticsCriteria criteria);
    List<GlobalKpiDTO> getGlobalKpis(StatisticsCriteria criteria);
    void calculateDailyStatistics(Long clinicId, LocalDate date);
}