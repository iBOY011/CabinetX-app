package com.gi.prescriptionservice.service;

import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;

import java.util.List;

public interface PrescriptionService {

    PrescriptionDTO createPrescription(PrescriptionDTO dto);

    PrescriptionDTO addLine(Long prescriptionId, PrescriptionLineDTO lineDTO);

    void removeLine(Long lineId);

    PrescriptionDTO findById(Long id);

    List<PrescriptionDTO> findByPatientId(Long patientId);

    byte[] generateMedicationPdf(Long prescriptionId);

    byte[] generateExamPdf(Long prescriptionId);
}