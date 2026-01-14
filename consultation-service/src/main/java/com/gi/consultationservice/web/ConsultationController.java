package com.gi.consultationservice.web;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.dto.MedecinWeeklyStatsDTO;
import com.gi.consultationservice.enums.ConsultationType;
import com.gi.consultationservice.service.ConsultationService;

import jakarta.validation.Valid;

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
        System.out.println("dto: "+dto.toString());
        return consultationService.creerConsultation(dto);
    }

    @PutMapping("/{id}")
    public ConsultationDTO modifierConsultation(@PathVariable Long id, @RequestBody @Valid ConsultationDTO dto) {
        System.out.println("[ConsultationController] ===== PUT /api/consultations/" + id + " =====");
        System.out.println("[ConsultationController] Received DTO: " + dto);
        System.out.println("[ConsultationController] PatientId: " + dto.getPatientId());
        System.out.println("[ConsultationController] MedecinId: " + dto.getMedecinId());
        System.out.println("[ConsultationController] CabinetId: " + dto.getCabinetId());
        System.out.println("[ConsultationController] RendezVousId: " + dto.getRendezVousId());
        System.out.println("[ConsultationController] Type: " + dto.getType());
        System.out.println("[ConsultationController] Archived: " + dto.getArchived());
        ConsultationDTO result = consultationService.modifierConsultation(id, dto);
        System.out.println("[ConsultationController] Returning: " + result);
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerConsultation(@PathVariable Long id) {
        consultationService.supprimerConsultation(id);
    }

    @GetMapping("/{id}")
    public ConsultationDTO trouverParId(@PathVariable Long id) {
        return consultationService.trouverParId(id);
    }

    @GetMapping("/rendezvous/{rendezVousId}")
    public ConsultationDTO trouverParRendezVous(@PathVariable Long rendezVousId) {
        return consultationService.trouverParRendezVous(rendezVousId);
    }

    @GetMapping
    public List<ConsultationDTO> listerToutes() {
        return consultationService.listerToutes();
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

    @GetMapping("/medecins/{medecinId}/recent")
    public List<ConsultationSummaryDTO> consultationsRecents(@PathVariable Long medecinId,
                                                             @RequestParam(defaultValue = "5") int limit) {
        int effectiveLimit = limit > 0 ? limit : 5;
        System.out.println("[ConsultationController] recent consultations medecinId=" + medecinId + " limitRequested=" + limit + " effectiveLimit=" + effectiveLimit);
        List<ConsultationSummaryDTO> result = consultationService.listerRecentsParMedecin(medecinId, effectiveLimit);
        System.out.println("[ConsultationController] recent consultations result count=" + result.size());
        return result;
    }

    @GetMapping("/medecins/{medecinId}")
    public Page<ConsultationSummaryDTO> consultationsPaged(@PathVariable Long medecinId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "dateConsultation,DESC") String sort,
                                                           @RequestParam(required = false) Long patientId,
                                                           @RequestParam(required = false) Boolean archived,
                                                           @RequestParam(required = false) ConsultationType type) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), parseSort(sort));
        return consultationService.listerParMedecinPaged(medecinId, patientId, archived, type, pageable);
    }

    @GetMapping("/stats/medecin/{medecinId}/weekly")
    public MedecinWeeklyStatsDTO statsMedecinWeekly(@PathVariable Long medecinId,
                                                    @RequestParam(required = false) Long cabinetId,
                                                    @RequestParam(required = false) LocalDate startDate,
                                                    @RequestParam(required = false) LocalDate endDate) {
        return consultationService.statsMedecinWeekly(medecinId, cabinetId, startDate, endDate);
    }

    @GetMapping("/auth")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Order.desc("dateConsultation"));
        }
        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim().toUpperCase() : "ASC";
        Sort.Order order = "DESC".equals(direction) ? Sort.Order.desc(property) : Sort.Order.asc(property);
        return Sort.by(order);
    }
}
