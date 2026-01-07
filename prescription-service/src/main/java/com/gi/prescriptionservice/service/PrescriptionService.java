package com.gi.prescriptionservice.service;

import java.util.List;

import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;

public interface PrescriptionService {

    PrescriptionDTO createPrescription(PrescriptionDTO dto);

    PrescriptionDTO addLine(Long prescriptionId, PrescriptionLineDTO lineDTO);

    void removeLine(Long lineId);

    PrescriptionDTO findById(Long id);

    PrescriptionDTO findByConsultationId(Long consultationId);

    List<PrescriptionDTO> findByPatientId(Long patientId);

    List<PrescriptionDTO> findByClinicAndDateRange(Long clinicId, java.time.LocalDate start, java.time.LocalDate end);

    byte[] generateMedicationPdf(Long prescriptionId);

    byte[] generateExamPdf(Long prescriptionId);
}