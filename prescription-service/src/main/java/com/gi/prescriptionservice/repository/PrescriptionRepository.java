package com.gi.prescriptionservice.repository;

import com.gi.prescriptionservice.model.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByConsultationId(Long consultationId);

    List<Prescription> findByPatientId(Long patientId);

    long countByDoctorIdAndPrescriptionDateBetween(Long doctorId, LocalDate start, LocalDate end);

    long countByDoctorIdAndClinicIdAndPrescriptionDateBetween(Long doctorId, Long clinicId, LocalDate start, LocalDate end);
}