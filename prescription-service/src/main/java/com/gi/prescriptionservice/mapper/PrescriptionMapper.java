package com.gi.prescriptionservice.mapper;

import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;
import com.gi.prescriptionservice.model.entity.Prescription;
import com.gi.prescriptionservice.model.entity.PrescriptionLine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrescriptionMapper {

    public PrescriptionDTO toDTO(Prescription prescription) {
        if (prescription == null) {
            return null;
        }
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setConsultationId(prescription.getConsultationId());
        dto.setPatientId(prescription.getPatientId());
        dto.setDoctorId(prescription.getDoctorId());
        dto.setClinicId(prescription.getClinicId());
        dto.setPrescriptionDate(prescription.getPrescriptionDate());
        dto.setDigitalSignature(prescription.getDigitalSignature());
        dto.setLines(toLineDTOs(prescription.getLines()));
        return dto;
    }

    public Prescription toEntity(PrescriptionDTO dto) {
        if (dto == null) {
            return null;
        }
        Prescription prescription = new Prescription();
        prescription.setId(dto.getId());
        prescription.setConsultationId(dto.getConsultationId());
        prescription.setPatientId(dto.getPatientId());
        prescription.setDoctorId(dto.getDoctorId());
        prescription.setClinicId(dto.getClinicId());
        prescription.setPrescriptionDate(dto.getPrescriptionDate());
        prescription.setDigitalSignature(dto.getDigitalSignature());
        prescription.setLines(toLineEntities(dto.getLines(), prescription));
        return prescription;
    }

    public PrescriptionLineDTO toDTO(PrescriptionLine line) {
        if (line == null) {
            return null;
        }
        PrescriptionLineDTO dto = new PrescriptionLineDTO();
        dto.setId(line.getId());
        dto.setPrescriptionId(line.getPrescriptionId());
        dto.setLineType(line.getLineType());
        dto.setMedicationId(line.getMedicationId());
        dto.setMedicationName(line.getMedicationName());
        dto.setDosage(line.getDosage());
        dto.setDurationDays(line.getDurationDays());
        dto.setComment(line.getComment());
        return dto;
    }

    public PrescriptionLine toEntity(PrescriptionLineDTO dto, Prescription prescription) {
        if (dto == null) {
            return null;
        }
        PrescriptionLine line = new PrescriptionLine();
        line.setId(dto.getId());
        line.setPrescriptionId(prescription.getId());
        line.setLineType(dto.getLineType());
        line.setMedicationId(dto.getMedicationId());
        line.setMedicationName(dto.getMedicationName());
        line.setDosage(dto.getDosage());
        line.setDurationDays(dto.getDurationDays());
        line.setComment(dto.getComment());
        line.setPrescription(prescription);
        return line;
    }

    private List<PrescriptionLineDTO> toLineDTOs(List<PrescriptionLine> lines) {
        return lines != null ? lines.stream().map(this::toDTO).collect(Collectors.toList()) : null;
    }

    private List<PrescriptionLine> toLineEntities(List<PrescriptionLineDTO> dtos, Prescription prescription) {
        return dtos != null ? dtos.stream().map(dto -> toEntity(dto, prescription)).collect(Collectors.toList()) : null;
    }
}