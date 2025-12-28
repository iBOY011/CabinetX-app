package com.gi.analyticsservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalKpiDTO {
    private LocalDate date;
    private int numberOfActiveClinics;
    private int totalNumberOfUsers;
    private int totalNumberOfAppointments;
}