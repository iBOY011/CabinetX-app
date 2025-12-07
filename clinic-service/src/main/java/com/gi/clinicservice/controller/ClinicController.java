package com.gi.clinicservice.controller;

import com.gi.clinicservice.model.dto.ClinicDTO;
import com.gi.clinicservice.service.ClinicService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService service;

    @PostMapping
    public ResponseEntity<ClinicDTO> createClinic(@Valid @RequestBody ClinicDTO request) {
        return ResponseEntity.ok(service.createClinic(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicDTO> updateClinic(@PathVariable Long id, @Valid @RequestBody ClinicDTO request) {
        return ResponseEntity.ok(service.updateClinic(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ClinicDTO> activateClinic(@PathVariable Long id) {
        return ResponseEntity.ok(service.activateClinic(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ClinicDTO> deactivateClinic(@PathVariable Long id) {
        return ResponseEntity.ok(service.deactivateClinic(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClinicDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ClinicDTO>> findActive() {
        return ResponseEntity.ok(service.findActive());
    }

    @GetMapping("/near-expiration")
    public ResponseEntity<List<ClinicDTO>> findNearExpiration(@RequestParam(defaultValue = "30") int daysBefore) {
        return ResponseEntity.ok(service.findNearExpiration(daysBefore));
    }
}