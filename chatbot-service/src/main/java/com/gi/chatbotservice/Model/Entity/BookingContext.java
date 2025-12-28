package com.gi.chatbotservice.Model.Entity;

import com.gi.chatbotservice.Model.Enum.BookingState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "booking_contexts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Long cabinetId;

    private String nomPatient;

    private String telephone;

    private LocalDate dateSouhaitee;

    private LocalTime creneauChoisi;

    @Enumerated(EnumType.STRING)
    private BookingState etat;

    private Long rendezVousId;
}
