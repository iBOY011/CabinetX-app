package com.gi.medicalrecordservice.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "documents")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long patientId;

    private String medicalHistory;
    private String allergies;
    private String treatments;
    private String habits;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    @Builder.Default
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedicalDocument> documents = new ArrayList<>();

    @PrePersist
    void prePersist() {
        creationDate = LocalDateTime.now();
        lastUpdate = creationDate;
    }

    @PreUpdate
    void preUpdate() {
        lastUpdate = LocalDateTime.now();
    }

    public void addDocument(MedicalDocument document) {
        document.setMedicalRecord(this);
        documents.add(document);
    }
}