package com.gi.medicalrecordservice.dto;

import com.gi.medicalrecordservice.enums.ConsultationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationInfo {
    private Long id;
    private LocalDateTime dateConsultation;
    private ConsultationType type;
    private String diagnostic;
    private String traitement;
}
