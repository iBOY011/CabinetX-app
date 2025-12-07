package com.gi.consultationservice.dto;

import com.gi.consultationservice.enums.ConsultationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationSummaryDTO {
    private Long id;
    private LocalDateTime dateConsultation;
    private ConsultationType type;
    private String diagnostic;
}
