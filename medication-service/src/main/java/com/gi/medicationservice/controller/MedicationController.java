package com.gi.medicationservice.controller;

import com.gi.medicationservice.model.dto.MedicationDTO;
import com.gi.medicationservice.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur REST pour la gestion du catalogue de médicaments.
 * 
 * <p>Expose des endpoints pour gérer le référentiel de médicaments utilisé 
 * lors de la création d'ordonnances. Permet recherche autocomplete pour faciliter 
 * la saisie des médecins.</p>
 * 
 * <p><b>Endpoints disponibles :</b></p>
 * <ul>
 *   <li>POST /api/medications - Créer un médicament</li>
 *   <li>PUT /api/medications/{id} - Mettre à jour un médicament</li>
 *   <li>DELETE /api/medications/{id} - Supprimer un médicament</li>
 *   <li>GET /api/medications/{id} - Récupérer un médicament par ID</li>
 *   <li>GET /api/medications - Liste tous les médicaments</li>
 *   <li>GET /api/medications/autocomplete?term=para - Recherche autocomplete (nom commercial + DCI)</li>
 *   <li>POST /api/medications/import - Import en masse (CSV/Excel/JSON)</li>
 * </ul>
 * 
 * <p><b>Sécurité :</b></p>
 * <ul>
 *   <li>MEDECIN : Lecture seule (GET /autocomplete, GET /{id})</li>
 *   <li>ADMIN : CRUD complet + import</li>
 * </ul>
 * 
 * <p><b>Cas d'usage autocomplete :</b></p>
 * <pre>
 * Frontend saisit "para" → Backend retourne :
 * - Paracétamol 500mg (comprimé)
 * - Paracétamol 1g (comprimé effervescent)
 * - Paracod (paracétamol + codéine)
 * </pre>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
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