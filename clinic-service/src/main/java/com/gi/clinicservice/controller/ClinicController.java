package com.gi.clinicservice.controller;

import com.gi.clinicservice.model.dto.request.ClinicRequest;
import com.gi.clinicservice.model.dto.response.ClinicResponse;
import com.gi.clinicservice.service.ClinicService;
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
    public ResponseEntity<ClinicResponse> createClinic(@RequestBody ClinicRequest request) {
        return ResponseEntity.ok(service.createClinic(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicResponse> updateClinic(@PathVariable Long id, @RequestBody ClinicRequest request) {
        return ResponseEntity.ok(service.updateClinic(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ClinicResponse> activateClinic(@PathVariable Long id) {
        return ResponseEntity.ok(service.activateClinic(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ClinicResponse> deactivateClinic(@PathVariable Long id) {
        return ResponseEntity.ok(service.deactivateClinic(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ClinicResponse>> findActive() {
        return ResponseEntity.ok(service.findActive());
    }

    @GetMapping("/near-expiration")
    public ResponseEntity<List<ClinicResponse>> findNearExpiration(@RequestParam(defaultValue = "30") int daysBefore) {
        return ResponseEntity.ok(service.findNearExpiration(daysBefore));
    }
}