package com.gi.medicalrecordservice.service;

import com.gi.medicalrecordservice.model.dto.response.ConsultationInfo;
import com.gi.medicalrecordservice.model.dto.response.MedicalDocumentDTO;
import com.gi.medicalrecordservice.model.dto.response.MedicalRecordDTO;
import com.gi.medicalrecordservice.model.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MedicalRecordService {
    MedicalRecordDTO openOrCreateRecord(Long patientId);
    MedicalRecordDTO updateRecord(Long recordId, MedicalRecordDTO dto);
    MedicalRecordDTO getCompleteRecord(Long patientId);
    MedicalDocumentDTO addDocument(Long recordId, MultipartFile file, DocumentType documentType);
    void deleteDocument(Long documentId);
    List<ConsultationInfo> getConsultationHistory(Long patientId);
}