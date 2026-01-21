package com.gi.clinicservice.controller;

import com.gi.clinicservice.model.dto.ClinicDTO;
import com.gi.clinicservice.service.ClinicService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des cabinets médicaux.
 * 
 * <p>Expose des endpoints pour créer, modifier, activer/désactiver et consulter les cabinets.
 * Chaque cabinet représente une structure médicale (cabinet dentaire, cabinet dermatologique, etc.)
 * avec une date de fin d'abonnement (serviceEndDate) et un statut (ACTIVE/INACTIVE).</p>
 * 
 * <p><b>Endpoints disponibles :</b></p>
 * <ul>
 *   <li>POST /api/clinics - Créer un nouveau cabinet</li>
 *   <li>PUT /api/clinics/{id} - Mettre à jour un cabinet</li>
 *   <li>PATCH /api/clinics/{id}/activate - Activer un cabinet</li>
 *   <li>PATCH /api/clinics/{id}/deactivate - Désactiver un cabinet</li>
 *   <li>GET /api/clinics/{id} - Récupérer un cabinet par ID</li>
 *   <li>GET /api/clinics - Liste tous les cabinets</li>
 *   <li>GET /api/clinics/active - Liste cabinets actifs uniquement</li>
 *   <li>GET /api/clinics/near-expiration?daysBefore=30 - Cabinets proches expiration</li>
 * </ul>
 * 
 * <p><b>Sécurité :</b> Requiert rôle ADMIN (configuré dans SecurityConfig). 
 * Les utilisateurs MEDECIN/SECRETAIRE ne peuvent que consulter leur propre cabinet.</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
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
        System.out.println("[ClinicController] GET /api/clinics/" + id);
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