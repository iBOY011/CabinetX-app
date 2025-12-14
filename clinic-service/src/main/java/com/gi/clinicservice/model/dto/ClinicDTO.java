package com.gi.clinicservice.model.dto;

import com.gi.clinicservice.model.enums.ClinicStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String specialty;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank
    private String address;

    private String logoUrl;

    @NotBlank
    private ClinicStatus status = ClinicStatus.ACTIVE;

    @NotBlank
    private LocalDate serviceEndDate;
}