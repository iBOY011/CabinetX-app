package com.gi.appointmentservice.Controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.DTO.UpdateDto;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Service.RDVService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
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

    @PostMapping("/rendezvous")
    public ResponseEntity<RDVResponse> createRendezVous(@RequestBody RDVRequest request) {
        
        RDVResponse response = rdvService.createRendezVous(request);
        return ResponseEntity.ok(response);
    }

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

    @PutMapping("/updateStatus/{id}/{statut}")
    public ResponseEntity<RDVResponse> updateStatusRendezVous(@PathVariable Long id, @PathVariable StatutRDV statut) {
        RDVResponse response = rdvService.updateStatusRendezVous(id, statut);
        return ResponseEntity.ok(response);
    }



}
