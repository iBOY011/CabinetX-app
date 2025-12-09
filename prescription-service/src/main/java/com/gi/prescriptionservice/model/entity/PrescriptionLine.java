package com.gi.prescriptionservice.model.entity;

import com.gi.prescriptionservice.model.enums.PrescriptionLineType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescription_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long prescriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionLineType lineType;

    private Long medicationId;

    private String medicationName;

    private String dosage;

    private int durationDays;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescriptionId", insertable = false, updatable = false)
    private Prescription prescription;
}