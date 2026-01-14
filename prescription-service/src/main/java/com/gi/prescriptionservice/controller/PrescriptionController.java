package com.gi.prescriptionservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;
import com.gi.prescriptionservice.service.PrescriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDTO> createPrescription(@RequestBody PrescriptionDTO dto) {
        System.out.println("Received prescription data: " + dto);
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

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<PrescriptionDTO> findByConsultation(@PathVariable Long consultationId) {
        PrescriptionDTO dto = prescriptionService.findByConsultationId(consultationId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> findByPatientId(@PathVariable Long patientId) {
        List<PrescriptionDTO> dtos = prescriptionService.findByPatientId(patientId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/clinic/{clinicId}/this-week")
    public ResponseEntity<List<PrescriptionDTO>> findClinicPrescriptionsThisWeek(@PathVariable Long clinicId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate start = today.with(java.time.DayOfWeek.MONDAY);
        java.time.LocalDate end = today.with(java.time.DayOfWeek.SUNDAY);
        List<PrescriptionDTO> dtos = prescriptionService.findByClinicAndDateRange(clinicId, start, end);
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

    @GetMapping("/count")
    public ResponseEntity<Long> getPrescriptionCount(
            @RequestParam Long doctorId,
            @RequestParam(required = false) Long clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        long count = prescriptionService.countPrescriptions(doctorId, clinicId, startDate, endDate);
        return ResponseEntity.ok(count);
    }
}