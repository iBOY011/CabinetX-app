package com.gi.medicalrecordservice.repository;

import com.gi.medicalrecordservice.entities.DocumentMedical;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentMedicalRepository extends JpaRepository<DocumentMedical, Long> {
    List<DocumentMedical> findByDossierId(Long dossierId);
}
