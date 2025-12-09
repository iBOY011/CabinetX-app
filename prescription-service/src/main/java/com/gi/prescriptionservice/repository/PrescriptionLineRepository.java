package com.gi.prescriptionservice.repository;

import com.gi.prescriptionservice.model.entity.PrescriptionLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionLineRepository extends JpaRepository<PrescriptionLine, Long> {

    List<PrescriptionLine> findByPrescriptionId(Long prescriptionId);
}