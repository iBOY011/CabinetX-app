package com.gi.appointmentservice.Model.DTO;
import java.time.LocalDate;

import com.gi.appointmentservice.Model.Enum.StatutRDV;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class RDVResponse extends RDVRequest{

    private Long id;
    private String prenom;
    private String nom;
    private String cin;
    private LocalDate dateNaissance;
    private StatutRDV statutRDV;
    private Integer queuePosition;
    
}
