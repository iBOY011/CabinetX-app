package com.gi.appointmentservice.Model.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RDVRequest extends UpdateDto{

    @NotNull(message = "Le patient est obligatoire")
    @Positive(message = "ID du patient invalide")
    private Long patientId;
    
    @NotNull(message = "Le cabinet est obligatoire")
    @Positive(message = "ID du cabinet invalide")
    private Long cabinetId;

}
