package com.gi.medicalrecordservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    private Long id;
    private Long patientId;
    private String medicalHistory;
    private String allergies;
    private String treatments;
    private String habits;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    private List<MedicalDocumentDTO> documents;
    private List<ConsultationInfo> consultationHistory;
}