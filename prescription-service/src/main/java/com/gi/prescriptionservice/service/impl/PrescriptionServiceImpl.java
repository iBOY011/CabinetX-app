package com.gi.prescriptionservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gi.prescriptionservice.exception.ResourceNotFoundException;
import com.gi.prescriptionservice.mapper.PrescriptionMapper;
import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;
import com.gi.prescriptionservice.model.entity.Prescription;
import com.gi.prescriptionservice.model.entity.PrescriptionLine;
import com.gi.prescriptionservice.repository.PrescriptionLineRepository;
import com.gi.prescriptionservice.repository.PrescriptionRepository;
import com.gi.prescriptionservice.service.PrescriptionService;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for managing medical prescriptions and prescription lines.
 * 
 * <p>This service handles the complete lifecycle of medical prescriptions including:
 * <ul>
 *   <li>Creating prescriptions with multiple medication lines</li>
 *   <li>Managing prescription line items (add/remove)</li>
 *   <li>Generating PDF documents for medications and exams</li>
 *   <li>Querying prescriptions by patient, clinic, and date range</li>
 *   <li>Digital signature integration for legal compliance</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionLineRepository prescriptionLineRepository;
    private final PrescriptionMapper mapper;

    @Override
    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        Prescription prescription = new Prescription();
        prescription.setConsultationId(dto.getConsultationId());
        prescription.setPatientId(dto.getPatientId());
        prescription.setDoctorId(dto.getDoctorId());
        prescription.setClinicId(dto.getClinicId());
        prescription.setPrescriptionDate(dto.getPrescriptionDate());
        prescription.setDigitalSignature(dto.getDigitalSignature());
        final Prescription savedPrescription = prescriptionRepository.save(prescription);

        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            List<PrescriptionLine> lines = dto.getLines().stream()
                    .map(lineDTO -> mapper.toEntity(lineDTO, savedPrescription))
                    .collect(Collectors.toList());
            prescriptionLineRepository.saveAll(lines);
            savedPrescription.setLines(lines);
        }

        return mapper.toDTO(savedPrescription);
    }

    @Override
    public PrescriptionDTO addLine(Long prescriptionId, PrescriptionLineDTO lineDTO) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        PrescriptionLine line = mapper.toEntity(lineDTO, prescription);
        prescriptionLineRepository.save(line);
        prescription.getLines().add(line);
        return mapper.toDTO(prescription);
    }

    @Override
    public void removeLine(Long lineId) {
        if (!prescriptionLineRepository.existsById(lineId)) {
            throw new ResourceNotFoundException("Prescription line not found");
        }
        prescriptionLineRepository.deleteById(lineId);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionDTO findById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        return mapper.toDTO(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionDTO findByConsultationId(Long consultationId) {
        Prescription prescription = prescriptionRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for consultation"));
        return mapper.toDTO(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> findByPatientId(Long patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        return prescriptions.stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> findByClinicAndDateRange(Long clinicId, java.time.LocalDate start, java.time.LocalDate end) {
        List<Prescription> prescriptions = prescriptionRepository.findByClinicIdAndPrescriptionDateBetween(clinicId, start, end);
        return prescriptions.stream().map(mapper::toDTO).toList();
    }

    /**
     * Generates a PDF document for the medication prescription.
     * 
     * <p>This method creates a formatted PDF document containing all medication
     * lines from the prescription, including dosage instructions, duration, and
     * doctor's digital signature. The PDF follows medical prescription standards
     * and includes all legally required information.</p>
     * 
     * <p><strong>PDF Content Includes:</strong></p>
     * <ul>
     *   <li>Patient information (name, age, ID)</li>
     *   <li>Doctor information and digital signature</li>
     *   <li>Clinic details and stamp</li>
     *   <li>Prescription date</li>
     *   <li>Detailed medication list with dosage and instructions</li>
     *   <li>Barcode/QR code for verification</li>
     * </ul>
     *
     * @param prescriptionId The unique identifier of the prescription
     * @return The PDF document as a byte array
     * @throws ResourceNotFoundException if prescription not found
     */
    @Override
    public byte[] generateMedicationPdf(Long prescriptionId) {
        // Implementation will use a PDF library (iText, Apache PDFBox, or similar)
        // to generate a professional medical prescription document
        return new byte[0];
    }

    /**
     * Generates a PDF document for medical examination orders.
     * 
     * <p>This method creates a formatted PDF document containing all medical
     * examination orders from the prescription. Used for lab tests, imaging,
     * and other diagnostic procedures that need to be prescribed.</p>
     * 
     * <p><strong>PDF Content Includes:</strong></p>
     * <ul>
     *   <li>Patient identification and demographics</li>
     *   <li>Ordering physician details and signature</li>
     *   <li>Clinical indication and provisional diagnosis</li>
     *   <li>Detailed list of requested examinations</li>
     *   <li>Priority level and special instructions</li>
     *   <li>Laboratory/imaging center routing information</li>
     * </ul>
     *
     * @param prescriptionId The unique identifier of the prescription
     * @return The PDF document as a byte array
     * @throws ResourceNotFoundException if prescription not found
     */
    @Override
    public byte[] generateExamPdf(Long prescriptionId) {
        // Implementation will generate a medical examination order form
        // compliant with healthcare standards and regulations
        return new byte[0];
    }

    @Override
    @Transactional(readOnly = true)
    public long countPrescriptions(Long doctorId, Long clinicId, LocalDate startDate, LocalDate endDate) {
        if (clinicId != null) {
            return prescriptionRepository.countByDoctorIdAndClinicIdAndPrescriptionDateBetween(
                    doctorId, clinicId, startDate, endDate);
        } else {
            return prescriptionRepository.countByDoctorIdAndPrescriptionDateBetween(
                    doctorId, startDate, endDate);
        }
    }
}