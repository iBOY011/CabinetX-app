package com.gi.medicalrecordservice.controller;

import com.gi.medicalrecordservice.model.dto.response.ConsultationInfo;
import com.gi.medicalrecordservice.model.dto.response.MedicalDocumentDTO;
import com.gi.medicalrecordservice.model.dto.response.MedicalRecordDTO;
import com.gi.medicalrecordservice.model.enums.DocumentType;
import com.gi.medicalrecordservice.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping("/patients/{patientId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordDTO openOrCreateRecord(@PathVariable Long patientId) {
        return medicalRecordService.openOrCreateRecord(patientId);
    }

    @PutMapping("/{recordId}")
    public MedicalRecordDTO updateRecord(@PathVariable Long recordId,
                                         @RequestBody @Valid MedicalRecordDTO dto) {
        return medicalRecordService.updateRecord(recordId, dto);
    }

    @GetMapping("/patients/{patientId}")
    public MedicalRecordDTO getRecord(@PathVariable Long patientId) {
        return medicalRecordService.getCompleteRecord(patientId);
    }

    @PostMapping(value = "/{recordId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalDocumentDTO addDocument(@PathVariable Long recordId,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("type") DocumentType documentType) {
        return medicalRecordService.addDocument(recordId, file, documentType);
    }

    @DeleteMapping("/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable Long documentId) {
        medicalRecordService.deleteDocument(documentId);
    }

    @GetMapping("/patients/{patientId}/consultations")
    public List<ConsultationInfo> consultationHistory(@PathVariable Long patientId) {
        return medicalRecordService.getConsultationHistory(patientId);
    }
}