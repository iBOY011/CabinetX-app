package com.gi.medicalrecordservice.mapper;

import com.gi.medicalrecordservice.model.dto.response.ConsultationInfo;
import com.gi.medicalrecordservice.model.dto.response.MedicalDocumentDTO;
import com.gi.medicalrecordservice.model.dto.response.MedicalRecordDTO;
import com.gi.medicalrecordservice.model.entity.MedicalDocument;
import com.gi.medicalrecordservice.model.entity.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MedicalRecordMapper {

    public MedicalRecordDTO toDto(MedicalRecord medicalRecord,
                                   List<MedicalDocument> documents,
                                   List<ConsultationInfo> consultations) {
        return MedicalRecordDTO.builder()
                .id(medicalRecord.getId())
                .patientId(medicalRecord.getPatientId())
                .medicalHistory(medicalRecord.getMedicalHistory())
                .allergies(medicalRecord.getAllergies())
                .treatments(medicalRecord.getTreatments())
                .habits(medicalRecord.getHabits())
                .creationDate(medicalRecord.getCreationDate())
                .lastUpdate(medicalRecord.getLastUpdate())
                .documents(toDocumentDtoList(documents))
                .consultationHistory(consultations)
                .build();
    }

    public List<MedicalDocumentDTO> toDocumentDtoList(List<MedicalDocument> documents) {
        if (Objects.isNull(documents) || documents.isEmpty()) {
            return Collections.emptyList();
        }
        return documents.stream()
                .map(this::toDocumentDto)
                .collect(Collectors.toList());
    }

    public MedicalDocumentDTO toDocumentDto(MedicalDocument document) {
        if (document == null) {
            return null;
        }
        return MedicalDocumentDTO.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .fileUrl(document.getFileUrl())
                .documentType(document.getDocumentType())
                .additionDate(document.getAdditionDate())
                .build();
    }
}