package com.gi.medicationservice.controller;

import com.gi.medicationservice.model.dto.MedicationDTO;
import com.gi.medicationservice.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    @PostMapping
    public ResponseEntity<MedicationDTO> createMedication(@RequestBody MedicationDTO dto) {
        MedicationDTO created = medicationService.createMedication(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicationDTO> updateMedication(@PathVariable Long id, @RequestBody MedicationDTO dto) {
        MedicationDTO updated = medicationService.updateMedication(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationDTO> findById(@PathVariable Long id) {
        MedicationDTO dto = medicationService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<MedicationDTO>> autocomplete(@RequestParam String term) {
        List<MedicationDTO> results = medicationService.autocomplete(term);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<List<MedicationDTO>> findAll() {
        List<MedicationDTO> all = medicationService.findAll();
        return ResponseEntity.ok(all);
    }

    @PostMapping("/import")
    public ResponseEntity<Integer> importFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            int count = medicationService.importFromFile(bytes);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}