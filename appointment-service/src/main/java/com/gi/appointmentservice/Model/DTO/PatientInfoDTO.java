package com.gi.appointmentservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PatientInfoDTO {
    Long id;
    String patientFirstName;
    String patientLastName;
}
