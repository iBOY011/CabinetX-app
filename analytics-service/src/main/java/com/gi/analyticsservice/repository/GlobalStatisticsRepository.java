package com.gi.analyticsservice.repository;

import com.gi.analyticsservice.model.entity.GlobalStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GlobalStatisticsRepository extends JpaRepository<GlobalStatistics, LocalDate> {
    List<GlobalStatistics> findByDateBetween(LocalDate start, LocalDate end);
}