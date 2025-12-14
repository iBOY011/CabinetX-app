package com.gi.analyticsservice.model.dto.request;

import com.gi.analyticsservice.model.enums.PeriodFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsCriteria {
    private Long clinicId;
    private LocalDate startDate;
    private LocalDate endDate;
    private PeriodFilter period;
}