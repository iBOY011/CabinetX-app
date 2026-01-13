package com.gi.consultationservice.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MedecinWeeklyStatsDTO {
    Long medecinId;
    Long cabinetId;
    OffsetDateTime start;
    OffsetDateTime end;
    long consultationsCount;
    long ordonnancesCount;
    double presenceRate;
    List<DailyStatDTO> daily;
}
