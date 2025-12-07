package com.gi.billingservice.model.dto.request;

import lombok.Data;

@Data
public class ConsultationDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long cabinetId;
}