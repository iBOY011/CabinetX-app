package com.gi.analyticsservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicKpiDTO {
    private LocalDate date;
    private int numberOfConsultations;
    private int numberOfNewPatients;
    private BigDecimal revenue;
}