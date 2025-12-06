package com.gi.analyticsservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "clinic_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicStatistics {
    @EmbeddedId
    private ClinicStatisticsId id;

    @Column(name = "number_of_consultations")
    private int numberOfConsultations;

    @Column(name = "number_of_new_patients")
    private int numberOfNewPatients;

    private BigDecimal revenue;
}