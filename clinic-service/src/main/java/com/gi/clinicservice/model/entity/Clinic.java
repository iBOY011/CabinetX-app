package com.gi.clinicservice.model.entity;

import com.gi.clinicservice.model.enums.ClinicStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private String address;
    private String logoUrl;
    @Enumerated(EnumType.STRING)
    private ClinicStatus status;
    private LocalDate serviceEndDate;
}