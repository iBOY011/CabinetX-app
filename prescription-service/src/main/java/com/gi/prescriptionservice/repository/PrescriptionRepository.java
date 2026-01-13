package com.gi.prescriptionservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gi.prescriptionservice.model.entity.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByConsultationId(Long consultationId);

    List<Prescription> findByPatientId(Long patientId);

    List<Prescription> findByClinicIdAndPrescriptionDateBetween(Long clinicId, LocalDate start, LocalDate end);

    long countByDoctorIdAndPrescriptionDateBetween(Long doctorId, LocalDate start, LocalDate end);

    long countByDoctorIdAndClinicIdAndPrescriptionDateBetween(Long doctorId, Long clinicId, LocalDate start, LocalDate end);
}