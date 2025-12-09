package com.gi.medicationservice.repository;

import com.gi.medicationservice.model.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByCommercialNameContainingIgnoreCase(String term);

    List<Medication> findByDciContainingIgnoreCase(String term);
}