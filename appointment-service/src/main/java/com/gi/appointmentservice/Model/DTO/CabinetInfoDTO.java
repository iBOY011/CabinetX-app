package com.gi.appointmentservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CabinetInfoDTO {
    private Long id;
    private String nom;
    private String adresse;
    private Long medecinId; // The doctor ID associated with this cabinet
}
