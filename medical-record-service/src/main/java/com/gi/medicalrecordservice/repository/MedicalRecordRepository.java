package com.gi.medicalrecordservice.repository;

import com.gi.medicalrecordservice.model.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByPatientId(Long patientId);
}