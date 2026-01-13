package com.gi.consultationservice.dto;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.gi.consultationservice.enums.ConsultationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationSummaryDTO {
    private Long id;
    private OffsetDateTime dateConsultation;
    private ConsultationType type;
    private String diagnostic;
    private String traitement;
    private String observations;
    private Boolean archived;
    private ZonedDateTime archivedAt;
}
