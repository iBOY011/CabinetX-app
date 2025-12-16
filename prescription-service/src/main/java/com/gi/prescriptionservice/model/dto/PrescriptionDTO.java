package com.gi.prescriptionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    private Long id;
    private Long consultationId;
    private Long patientId;
    private Long doctorId;
    private Long clinicId;
    private java.time.LocalDate prescriptionDate;
    private List<PrescriptionLineDTO> lines;
}