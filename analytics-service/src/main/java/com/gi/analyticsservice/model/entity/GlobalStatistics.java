package com.gi.analyticsservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "global_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatistics {
    @Id
    private LocalDate date;

    @Column(name = "number_of_active_clinics")
    private int numberOfActiveClinics;

    @Column(name = "total_number_of_users")
    private int totalNumberOfUsers;

    @Column(name = "total_number_of_appointments")
    private int totalNumberOfAppointments;
}