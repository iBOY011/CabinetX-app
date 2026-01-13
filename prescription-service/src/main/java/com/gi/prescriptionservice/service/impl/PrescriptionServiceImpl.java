package com.gi.prescriptionservice.service.impl;

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

    @Override
    public byte[] generateMedicationPdf(Long prescriptionId) {
        // TODO: Implement PDF generation for medications
        return new byte[0];
    }

    @Override
    public byte[] generateExamPdf(Long prescriptionId) {
        // TODO: Implement PDF generation for exams
        return new byte[0];
    }
}