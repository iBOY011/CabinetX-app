package com.gi.analyticsservice.repository;

import com.gi.analyticsservice.model.entity.ClinicStatistics;
import com.gi.analyticsservice.model.entity.ClinicStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ClinicStatisticsRepository extends JpaRepository<ClinicStatistics, ClinicStatisticsId> {
    List<ClinicStatistics> findByIdClinicIdAndIdDateBetween(Long clinicId, LocalDate start, LocalDate end);
}