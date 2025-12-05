package com.gi.chatbotservice.Model.DTO;

import com.gi.chatbotservice.Model.Enum.BookingState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingContextDTO {

    private Long id;

    private Long cabinetId;

    private String cabinetName;

    private Long doctorId;

    private String doctorName;

    private String nomPatient;

    private String telephone;

    private String email;

    private LocalDate dateSouhaitee;

    private LocalTime creneauChoisi;

    private BookingState etat;

    private Long rendezVousId;

    private boolean hasCriticalInfo;

    private String missingInfo;
}
