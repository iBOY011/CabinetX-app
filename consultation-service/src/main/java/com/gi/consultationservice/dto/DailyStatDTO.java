package com.gi.consultationservice.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DailyStatDTO {
    LocalDate date;
    long consultations;
    long ordonnances;
    double presenceRate;
}
