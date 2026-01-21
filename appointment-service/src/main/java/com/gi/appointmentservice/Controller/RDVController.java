package com.gi.appointmentservice.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.DTO.UpdateDto;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Service.RDVService;

import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST pour la gestion des rendez-vous médicaux.
 * 
 * <p>Expose les endpoints suivants :
 * <ul>
 *   <li>POST /api/appointments/rendezvous : Création de rendez-vous</li>
 *   <li>GET /api/appointments/{id} : Consultation d'un rendez-vous</li>
 *   <li>GET /api/appointments/patients/{patientId}/today : RDV du jour pour un patient</li>
 *   <li>PUT /api/appointments/update/{id} : Modification d'un rendez-vous</li>
 *   <li>PUT /api/appointments/updateStatus/{id}/{statut} : Changement de statut</li>
 *   <li>DELETE /api/appointments/delete/{id} : Suppression d'un rendez-vous</li>
 *   <li>GET /api/appointments/by-date : Liste des RDV par date et cabinet</li>
 * </ul>
 * 
 * <p>Tous les endpoints retournent des RDVResponse enrichis avec les informations
 * complètes du patient (nom, prénom, CIN, etc.).
 * 
 * <p>Sécurité : Les endpoints sont protégés par la gateway (authentification JWT).
 * Les rôles SECRETAIRE et MEDECIN peuvent accéder à ces ressources.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
public class RDVController {

    private final RDVService rdvService;
    @Autowired
    private final StreamBridge streamBridge;

    @GetMapping("/")
    public String welcome() {
        String message = "Welcome endpoint called!";

        // Publish message to Kafka
        streamBridge.send("queue-topic", message);

        return "Welcome to the Appointment Service!";
    }

    /**
     * Crée un nouveau rendez-vous.
     * 
     * @param request les informations du rendez-vous (patientId, cabinetId, date, heures, motif)
     * @return 200 OK avec RDVResponse si création réussie
     * @throws IllegalArgumentException si validation échouée (400 Bad Request)
     */
    @PostMapping("/rendezvous")
    public ResponseEntity<RDVResponse> createRendezVous(@RequestBody RDVRequest request) {

        RDVResponse response = rdvService.createRendezVous(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère un rendez-vous par son identifiant.
     * 
     * @param id identifiant du rendez-vous
     * @return 200 OK avec RDVResponse incluant les informations patient
     * @throws ResourceNotFoundException si le rendez-vous n'existe pas (404 Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RDVResponse> getRendezVousById(@PathVariable Long id) {
        RDVResponse response = rdvService.getRendezVousById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}/today")
    public ResponseEntity<RDVResponse> getTodayRendezVousForPatient(@PathVariable Long patientId) {
        RDVResponse response = rdvService.getTodayRendezVousForPatient(patientId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RDVResponse> updateRendezVous(@PathVariable Long id, @RequestBody UpdateDto request) {
        RDVResponse response = rdvService.updateRendezVous(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        rdvService.deleteRendezVous(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Met à jour le statut d'un rendez-vous.
     * 
     * <p>Statuts possibles : CONFIRME, EN_ATTENTE, EN_CONSULTATION, TERMINE, ANNULE, MISSING
     * 
     * @param id identifiant du rendez-vous
     * @param statut nouveau statut à appliquer
     * @return 200 OK avec RDVResponse si mise à jour réussie
     * @throws IllegalArgumentException si transition de statut invalide (400 Bad Request)
     */
    @PutMapping("/updateStatus/{id}/{statut}")
    public ResponseEntity<RDVResponse> updateStatusRendezVous(@PathVariable Long id, @PathVariable StatutRDV statut) {
        RDVResponse response = rdvService.updateStatusRendezVous(id, statut);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<RDVResponse>> getAppointmentsByDate(
            @RequestParam LocalDate date,
            @RequestParam Long cabinetId) {
        List<RDVResponse> appointments = rdvService.getAppointmentsByDate(date, cabinetId);
        return ResponseEntity.ok(appointments);
    }

}
