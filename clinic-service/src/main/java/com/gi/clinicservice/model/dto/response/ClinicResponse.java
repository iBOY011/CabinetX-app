package com.gi.clinicservice.model.dto.response;

import com.gi.clinicservice.model.enums.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicResponse {
    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private String address;
    private String logoUrl;
    private ClinicStatus status;
    private LocalDate serviceEndDate;
}