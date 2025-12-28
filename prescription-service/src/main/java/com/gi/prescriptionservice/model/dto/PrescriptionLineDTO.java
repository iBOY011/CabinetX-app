package com.gi.prescriptionservice.model.dto;

import com.gi.prescriptionservice.model.enums.PrescriptionLineType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionLineDTO {

    private Long id;
    private Long prescriptionId;
    private PrescriptionLineType lineType;
    private Long medicationId;
    private String medicationName;
    private String dosage;
    private int durationDays;
    private String comment;
}