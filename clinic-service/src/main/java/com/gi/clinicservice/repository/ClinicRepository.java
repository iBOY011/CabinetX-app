package com.gi.clinicservice.repository;

import com.gi.clinicservice.model.entity.Clinic;
import com.gi.clinicservice.model.enums.ClinicStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    List<Clinic> findByStatus(ClinicStatus status);
    List<Clinic> findByServiceEndDateBefore(LocalDate date);
}