package com.gi.consultationservice.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.entities.Consultation;
import com.gi.consultationservice.entities.ConsultationCreatedEvent;
import com.gi.consultationservice.mappers.ConsultationMapper;
import com.gi.consultationservice.repository.ConsultationEventRepository;
import com.gi.consultationservice.repository.ConsultationRepository;

@Service
@Transactional
public class ConsultationService {

    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;

    private final ConsultationRepository consultationRepository;
    private final ConsultationEventRepository eventRepository;
    private final ConsultationMapper consultationMapper;

    public ConsultationService(ConsultationRepository consultationRepository,
                               ConsultationEventRepository eventRepository,
                               ConsultationMapper consultationMapper) {
        this.consultationRepository = consultationRepository;
        this.eventRepository = eventRepository;
        this.consultationMapper = consultationMapper;
    }

    public ConsultationDTO creerConsultation(ConsultationDTO dto) {
        Consultation consultation = consultationMapper.toEntity(dto);
        consultation.setId(null);
        Consultation saved = consultationRepository.save(consultation);
        enregistrerCreation(saved);
        return consultationMapper.toDTO(saved);
    }

    public ConsultationDTO modifierConsultation(Long id, ConsultationDTO dto) {
        Consultation existing = chargerConsultation(id);
        appliquerChangements(existing, dto);
        Consultation updated = consultationRepository.save(existing);
        return consultationMapper.toDTO(updated);
    }

    public void supprimerConsultation(Long id) {
        Consultation existing = chargerConsultation(id);
        consultationRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public ConsultationDTO trouverParId(Long id) {
        return consultationMapper.toDTO(chargerConsultation(id));
    }

    @Transactional(readOnly = true)
    public ConsultationDTO trouverParRendezVous(Long rendezVousId) {
        return consultationRepository.findByRendezVousId(rendezVousId)
                .map(consultationMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation introuvable"));
    }

    @Transactional(readOnly = true)
    public List<ConsultationSummaryDTO> listerParPatient(Long patientId) {
        return consultationMapper.toSummaryList(
                consultationRepository.findByPatientIdOrderByDateConsultationDesc(patientId)
        );
    }

    @Transactional(readOnly = true)
    public List<ConsultationSummaryDTO> listerParMedecinEtJour(Long medecinId, LocalDate date) {
        OffsetDateTime debut = date.atStartOfDay().atOffset(DEFAULT_ZONE_OFFSET);
        OffsetDateTime fin = date.plusDays(1).atStartOfDay().minusNanos(1).atOffset(DEFAULT_ZONE_OFFSET);
        return listerParMedecinEtPeriode(medecinId, debut, fin);
    }

    @Transactional(readOnly = true)
    public List<ConsultationSummaryDTO> listerParMedecinEtPeriode(Long medecinId, OffsetDateTime debut, OffsetDateTime fin) {
        List<Consultation> consultations = consultationRepository
            .findByMedecinIdAndDateConsultationBetweenOrderByDateConsultationAsc(
                medecinId,
                debut,
                fin);
        return consultationMapper.toSummaryList(consultations);
    }

    private Consultation chargerConsultation(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation introuvable"));
    }

    private void appliquerChangements(Consultation consultation, ConsultationDTO dto) {
        consultation.setRendezVousId(dto.getRendezVousId());
        consultation.setPatientId(dto.getPatientId());
        consultation.setMedecinId(dto.getMedecinId());
        consultation.setCabinetId(dto.getCabinetId());
        consultation.setType(dto.getType());
        consultation.setDateConsultation(dto.getDateConsultation());
        consultation.setExamenClinique(dto.getExamenClinique());
        consultation.setExamenSupplementaire(dto.getExamenSupplementaire());
        consultation.setDiagnostic(dto.getDiagnostic());
        consultation.setTraitement(dto.getTraitement());
        consultation.setObservations(dto.getObservations());
    }

    private void enregistrerCreation(Consultation consultation) {
        eventRepository.save(ConsultationCreatedEvent.builder()
                .consultationId(consultation.getId())
                .patientId(consultation.getPatientId())
                .medecinId(consultation.getMedecinId())
                .rendezVousId(consultation.getRendezVousId())
                .dateConsultation(consultation.getDateConsultation())
                .build());
    }
}
