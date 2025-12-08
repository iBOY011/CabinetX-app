package com.gi.medicationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {

    private Long id;

    private String commercialName;

    private String dci;

    private String dosage;

    private String form;
}