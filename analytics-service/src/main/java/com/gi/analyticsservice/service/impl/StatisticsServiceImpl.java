package com.gi.analyticsservice.service.impl;

import com.gi.analyticsservice.model.dto.request.StatisticsCriteria;
import com.gi.analyticsservice.model.dto.response.ClinicKpiDTO;
import com.gi.analyticsservice.model.dto.response.GlobalKpiDTO;
import com.gi.analyticsservice.model.entity.ClinicStatistics;
import com.gi.analyticsservice.model.entity.GlobalStatistics;
import com.gi.analyticsservice.repository.ClinicStatisticsRepository;
import com.gi.analyticsservice.repository.GlobalStatisticsRepository;
import com.gi.analyticsservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final ClinicStatisticsRepository clinicStatisticsRepository;
    private final GlobalStatisticsRepository globalStatisticsRepository;

    @Override
    public List<ClinicKpiDTO> getClinicKpis(Long clinicId, StatisticsCriteria criteria) {
        List<ClinicStatistics> stats = clinicStatisticsRepository.findByIdClinicIdAndIdDateBetween(clinicId, criteria.getStartDate(), criteria.getEndDate());
        return stats.stream().map(stat -> {
            ClinicKpiDTO dto = new ClinicKpiDTO();
            dto.setDate(stat.getId().getDate());
            dto.setNumberOfConsultations(stat.getNumberOfConsultations());
            dto.setNumberOfNewPatients(stat.getNumberOfNewPatients());
            dto.setRevenue(stat.getRevenue());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<GlobalKpiDTO> getGlobalKpis(StatisticsCriteria criteria) {
        List<GlobalStatistics> stats = globalStatisticsRepository.findByDateBetween(criteria.getStartDate(), criteria.getEndDate());
        return stats.stream().map(stat -> {
            GlobalKpiDTO dto = new GlobalKpiDTO();
            dto.setDate(stat.getDate());
            dto.setNumberOfActiveClinics(stat.getNumberOfActiveClinics());
            dto.setTotalNumberOfUsers(stat.getTotalNumberOfUsers());
            dto.setTotalNumberOfAppointments(stat.getTotalNumberOfAppointments());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void calculateDailyStatistics(Long clinicId, LocalDate date) {
        // TODO: Implement calculation logic
        // This would involve querying other services or databases to get the data
        // For now, it's a placeholder
    }
}