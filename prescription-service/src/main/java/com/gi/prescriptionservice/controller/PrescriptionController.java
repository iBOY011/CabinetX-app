package com.gi.prescriptionservice.controller;

import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;
import com.gi.prescriptionservice.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDTO> createPrescription(@RequestBody PrescriptionDTO dto) {
        PrescriptionDTO created = prescriptionService.createPrescription(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{prescriptionId}/lines")
    public ResponseEntity<PrescriptionDTO> addLine(@PathVariable Long prescriptionId, @RequestBody PrescriptionLineDTO lineDTO) {
        PrescriptionDTO updated = prescriptionService.addLine(prescriptionId, lineDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> removeLine(@PathVariable Long lineId) {
        prescriptionService.removeLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> findById(@PathVariable Long id) {
        PrescriptionDTO dto = prescriptionService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> findByPatientId(@PathVariable Long patientId) {
        List<PrescriptionDTO> dtos = prescriptionService.findByPatientId(patientId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{prescriptionId}/medication-pdf")
    public ResponseEntity<byte[]> generateMedicationPdf(@PathVariable Long prescriptionId) {
        byte[] pdf = prescriptionService.generateMedicationPdf(prescriptionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "medication-prescription.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/{prescriptionId}/exam-pdf")
    public ResponseEntity<byte[]> generateExamPdf(@PathVariable Long prescriptionId) {
        byte[] pdf = prescriptionService.generateExamPdf(prescriptionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "exam-prescription.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}