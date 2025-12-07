package com.gi.appointmentservice.Model.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RDVRequest extends UpdateDto{

    private Long patientId;
    private Long cabinetId;
   
    

}
