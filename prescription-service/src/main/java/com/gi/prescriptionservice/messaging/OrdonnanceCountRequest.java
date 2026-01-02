package com.gi.prescriptionservice.messaging;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceCountRequest {
    private String correlationId;
    private Long medecinId;
    private Long cabinetId;
    private OffsetDateTime start;
    private OffsetDateTime end;
}
