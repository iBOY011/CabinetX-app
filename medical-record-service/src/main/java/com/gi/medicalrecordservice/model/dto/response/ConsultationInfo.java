package com.gi.medicalrecordservice.model.dto.response;

import com.gi.medicalrecordservice.model.enums.ConsultationType;
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
    private LocalDateTime consultationDate;
    private ConsultationType type;
    private String diagnosis;
    private String treatment;
}