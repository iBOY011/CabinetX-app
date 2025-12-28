package com.gi.consultationservice.web;

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")

public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationDTO creerConsultation(@RequestBody @Valid ConsultationDTO dto) {
        return consultationService.creerConsultation(dto);
    }

    @PutMapping("/{id}")
    public ConsultationDTO modifierConsultation(@PathVariable Long id, @RequestBody @Valid ConsultationDTO dto) {
        return consultationService.modifierConsultation(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerConsultation(@PathVariable Long id) {
        consultationService.supprimerConsultation(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ConsultationDTO trouverParId(@PathVariable Long id) {
        return consultationService.trouverParId(id);
    }

    @GetMapping("/rendezvous/{rendezVousId}")
    public ConsultationDTO trouverParRendezVous(@PathVariable Long rendezVousId) {
        return consultationService.trouverParRendezVous(rendezVousId);
    }

    @GetMapping("/patients/{patientId}/historique")
    public List<ConsultationSummaryDTO> historiquePatient(@PathVariable Long patientId) {
        return consultationService.listerParPatient(patientId);
    }

    @GetMapping("/medecins/{medecinId}/jour")
    public List<ConsultationSummaryDTO> consultationsJour(@PathVariable Long medecinId,
                                                          @RequestParam LocalDate date) {
        return consultationService.listerParMedecinEtJour(medecinId, date);
    }

    @GetMapping("/medecins/{medecinId}/periode")
    public List<ConsultationSummaryDTO> consultationsPeriode(@PathVariable Long medecinId,
                                                             @RequestParam OffsetDateTime debut,
                                                             @RequestParam OffsetDateTime fin) {
        return consultationService.listerParMedecinEtPeriode(medecinId, debut, fin);
    }

    @GetMapping("/auth")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }
}
