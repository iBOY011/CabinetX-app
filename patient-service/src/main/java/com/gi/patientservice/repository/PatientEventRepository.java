package com.gi.patientservice.repository;

import com.gi.patientservice.entities.PatientEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientEventRepository extends JpaRepository<PatientEvent, Long> {
    List<PatientEvent> findByPatientId(Long patientId);
}
