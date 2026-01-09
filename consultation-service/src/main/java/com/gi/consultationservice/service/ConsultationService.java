package com.gi.consultationservice.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gi.consultationservice.client.AppointmentClient;
import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.dto.DailyStatDTO;
import com.gi.consultationservice.dto.MedecinWeeklyStatsDTO;
import com.gi.consultationservice.entities.Consultation;
import com.gi.consultationservice.entities.ConsultationCreatedEvent;
import com.gi.consultationservice.enums.ConsultationType;
import com.gi.consultationservice.mappers.ConsultationMapper;
import com.gi.consultationservice.messaging.OrdonnanceCountClient;
import com.gi.consultationservice.repository.ConsultationEventRepository;
import com.gi.consultationservice.repository.ConsultationRepository;
import com.gi.consultationservice.client.AppointmentClient;
import com.gi.consultationservice.client.NotificationClient;
import com.gi.consultationservice.client.UserClient;
import com.gi.consultationservice.client.PatientClient;

@Service
@Transactional
public class ConsultationService {

    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationService.class);

    private final ConsultationRepository consultationRepository;
    private final ConsultationEventRepository eventRepository;
    private final ConsultationMapper consultationMapper;
    private final OrdonnanceCountClient ordonnanceCountClient;
    private final AppointmentClient appointmentClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;
    private final PatientClient patientClient;

    public ConsultationService(ConsultationRepository consultationRepository,
                               ConsultationEventRepository eventRepository,
                               ConsultationMapper consultationMapper,
                               OrdonnanceCountClient ordonnanceCountClient,
                               AppointmentClient appointmentClient,
                               NotificationClient notificationClient,
                               UserClient userClient,
                               PatientClient patientClient) {
        this.consultationRepository = consultationRepository;
        this.eventRepository = eventRepository;
        this.consultationMapper = consultationMapper;
        this.ordonnanceCountClient = ordonnanceCountClient;
        this.appointmentClient = appointmentClient;
        this.notificationClient = notificationClient;
        this.userClient = userClient;
        this.patientClient = patientClient;
    }

    public ConsultationDTO  creerConsultation(ConsultationDTO dto) {
        Consultation consultation = consultationMapper.toEntity(dto);
        System.out.println(dto);
        System.out.println(consultation);
        consultation.setId(null);
        Consultation saved = consultationRepository.save(consultation);
        enregistrerCreation(saved);
        return consultationMapper.toDTO(saved);
    }

    public ConsultationDTO modifierConsultation(Long id, ConsultationDTO dto) {
        System.out.println("[ConsultationService] Modifying consultation ID: " + id);
        System.out.println("[ConsultationService] DTO: " + dto);
        Consultation existing = chargerConsultation(id);
        System.out.println("[ConsultationService] Existing consultation: " + existing);
        System.out.println("[ConsultationService] Existing.archived: " + existing.getArchived());
        boolean wasNotArchived = existing.getArchived() == null || !existing.getArchived();
        System.out.println("[ConsultationService] wasNotArchived: " + wasNotArchived);
        appliquerChangements(existing, dto);
        System.out.println("[ConsultationService] After applying changes: " + existing);
        System.out.println("[ConsultationService] Updated.archived: " + existing.getArchived());
        Consultation updated = consultationRepository.save(existing);
        System.out.println("[ConsultationService] Saved consultation: " + updated);
        
        // If consultation is being archived (terminated), trigger notifications
        System.out.println("[ConsultationService] Check completion: wasNotArchived=" + wasNotArchived + ", dto.archived=" + dto.getArchived());
        if (wasNotArchived && dto.getArchived() != null && dto.getArchived()) {
            System.out.println("[ConsultationService] Triggering handleConsultationCompletion...");
            handleConsultationCompletion(updated);
        } else {
            System.out.println("[ConsultationService] Skipping handleConsultationCompletion");
        }
        
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
    public List<ConsultationDTO> listerToutes() {
        List<Consultation> consultations = consultationRepository.findAll(Sort.by(Sort.Order.desc("dateConsultation")));
        return consultationMapper.toDTOList(consultations);
    }

    @Transactional(readOnly = true)
    public List<ConsultationSummaryDTO> listerRecentsParMedecin(Long medecinId, int limit) {
        int effectiveLimit = Math.max(limit, 1);
        Pageable pageable = PageRequest.of(0, effectiveLimit, Sort.by(Sort.Order.desc("dateConsultation")));
        Page<Consultation> page = consultationRepository.findAll(specMedecin(medecinId), pageable);
        System.out.println(page);
        return consultationMapper.toSummaryList(page.getContent());
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

        @Transactional(readOnly = true)
        public MedecinWeeklyStatsDTO statsMedecinWeekly(Long medecinId,
                                Long cabinetId,
                                LocalDate startDate,
                                LocalDate endDate) {
        LocalDate start = startDate != null
            ? startDate
            : LocalDate.now(DEFAULT_ZONE_OFFSET).with(java.time.DayOfWeek.MONDAY);
        LocalDate end = endDate != null ? endDate : start.plusDays(6);
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate doit être après startDate");
        }

        OffsetDateTime debut = start.atStartOfDay().atOffset(DEFAULT_ZONE_OFFSET);
        OffsetDateTime fin = end.plusDays(1).atStartOfDay().minusNanos(1).atOffset(DEFAULT_ZONE_OFFSET);

        Specification<Consultation> spec = Specification.where(specMedecin(medecinId))
            .and(cabinetId != null ? specCabinet(cabinetId) : null)
            .and(specDateBetween(debut, fin));

        List<Consultation> consultations = consultationRepository.findAll(spec, Sort.by(Sort.Order.asc("dateConsultation")));

        Map<LocalDate, Long> perDay = consultations.stream()
            .collect(Collectors.groupingBy(c -> c.getDateConsultation().toLocalDate(), Collectors.counting()));

        List<DailyStatDTO> daily = new ArrayList<>();
        for (LocalDate cursor = start; !cursor.isAfter(end); cursor = cursor.plusDays(1)) {
            long dayConsultations = perDay.getOrDefault(cursor, 0L);
            daily.add(DailyStatDTO.builder()
                .date(cursor)
                .consultations(dayConsultations)
                .ordonnances(0L) // Ordonnances non gérées ici; intégrer un service dédié si disponible
                .presenceRate(dayConsultations > 0 ? 100.0 : 0.0)
                .build());
        }

        long consultationsCount = consultations.size();
        OptionalLong ordonnancesCountOptional = ordonnanceCountClient.getCountOrRequest(medecinId, cabinetId, debut, fin);
        long ordonnancesCount = ordonnancesCountOptional.orElse(-1L);
        double presenceRate = consultationsCount > 0 ? 100.0 : 0.0;

        return MedecinWeeklyStatsDTO.builder()
            .medecinId(medecinId)
            .cabinetId(cabinetId)
            .start(debut)
            .end(fin)
            .consultationsCount(consultationsCount)
            .ordonnancesCount(ordonnancesCount)
            .presenceRate(presenceRate)
            .daily(daily)
            .build();
        }

    @Transactional(readOnly = true)
    public Page<ConsultationSummaryDTO> listerParMedecinPaged(Long medecinId,
                                                             Long patientId,
                                                             Boolean archived,
                                                             ConsultationType type,
                                                             Pageable pageable) {
        Specification<Consultation> spec = Specification.where(specMedecin(medecinId))
                .and(patientId != null ? specPatient(patientId) : null)
                .and(archived != null ? specArchived(archived) : null)
                .and(type != null ? specType(type) : null);

        Page<Consultation> page = consultationRepository.findAll(spec, pageable);
        return page.map(consultationMapper::toSummary);
    }

    private Consultation chargerConsultation(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation introuvable"));
    }

    private void appliquerChangements(Consultation consultation, ConsultationDTO dto) {
        if (dto.getRendezVousId() != null) {
            consultation.setRendezVousId(dto.getRendezVousId());
        }
        if (dto.getPatientId() != null) {
            consultation.setPatientId(dto.getPatientId());
        }
        if (dto.getMedecinId() != null) {
            consultation.setMedecinId(dto.getMedecinId());
        }
        if (dto.getCabinetId() != null) {
            consultation.setCabinetId(dto.getCabinetId());
        }
        if (dto.getType() != null) {
            consultation.setType(dto.getType());
        }
        if (dto.getDateConsultation() != null) {
            consultation.setDateConsultation(dto.getDateConsultation());
        }
        consultation.setExamenClinique(dto.getExamenClinique());
        consultation.setExamenSupplementaire(dto.getExamenSupplementaire());
        consultation.setDiagnostic(dto.getDiagnostic());
        consultation.setTraitement(dto.getTraitement());
        consultation.setObservations(dto.getObservations());
        if (dto.getArchived() != null) {
            consultation.setArchived(dto.getArchived());
        }
        if (dto.getArchivedAt() != null || Boolean.FALSE.equals(dto.getArchived())) {
            consultation.setArchivedAt(dto.getArchivedAt());
        }
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

    private Specification<Consultation> specMedecin(Long medecinId) {
        return (root, query, cb) -> cb.equal(root.get("medecinId"), medecinId);
    }

    private Specification<Consultation> specCabinet(Long cabinetId) {
        return (root, query, cb) -> cb.equal(root.get("cabinetId"), cabinetId);
    }

    private Specification<Consultation> specPatient(Long patientId) {
        return (root, query, cb) -> cb.equal(root.get("patientId"), patientId);
    }

    private Specification<Consultation> specArchived(Boolean archived) {
        return (root, query, cb) -> cb.equal(root.get("archived"), archived);
    }

    private Specification<Consultation> specType(ConsultationType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    private Specification<Consultation> specDateBetween(OffsetDateTime debut, OffsetDateTime fin) {
        return (root, query, cb) -> cb.between(root.get("dateConsultation"), debut, fin);
    }

    private void handleConsultationCompletion(Consultation consultation) {
        System.out.println("[ConsultationService] ===== HANDLING CONSULTATION COMPLETION =====");
        System.out.println("[ConsultationService] Consultation ID: " + consultation.getId());
        System.out.println("[ConsultationService] Patient ID: " + consultation.getPatientId());
        System.out.println("[ConsultationService] Cabinet ID: " + consultation.getCabinetId());
        System.out.println("[ConsultationService] RendezVous ID: " + consultation.getRendezVousId());
        try {
            // 1. Mark appointment as completed
            if (consultation.getRendezVousId() != null) {
                System.out.println("[ConsultationService] Marking appointment " + consultation.getRendezVousId() + " as completed...");
                appointmentClient.markAppointmentAsCompleted(consultation.getRendezVousId());
                System.out.println("[ConsultationService] ✓ Marked appointment " + consultation.getRendezVousId() + " as TERMINE");
            } else {
                System.out.println("[ConsultationService] ⚠ No rendezVousId, skipping appointment update");
            }

            // 2. Get patient info
            System.out.println("[ConsultationService] Fetching patient info for ID: " + consultation.getPatientId());
            PatientClient.PatientDTO patient = patientClient.getPatientById(consultation.getPatientId());
            String patientName = patient.getPrenom() + " " + patient.getNom();
            System.out.println("[ConsultationService] ✓ Patient: " + patientName);

            // 3. Get all secretaries in the same cabinet
            System.out.println("[ConsultationService] Fetching secretaries for cabinet ID: " + consultation.getCabinetId());
            List<UserClient.UserDTO> secretaries = userClient.getUsersByCabinetAndRole(
                    consultation.getCabinetId(), "secretaire");
            System.out.println("[ConsultationService] ✓ Found " + secretaries.size() + " secretary/secretaries");

            // 4. Send notification to each secretary
            for (UserClient.UserDTO secretary : secretaries) {
                System.out.println("[ConsultationService] Sending billing notification to secretary " + secretary.getId() + " (" + secretary.getEmail() + ")");
                notificationClient.sendBillingReadyNotification(
                        secretary.getId(),
                        consultation.getId(),
                        consultation.getRendezVousId(),
                        consultation.getPatientId(),
                        patientName,
                        consultation.getDiagnostic(),
                        consultation.getTraitement()
                );
                System.out.println("[ConsultationService] ✓ Sent billing notification to secretary " + secretary.getId());
            }
            System.out.println("[ConsultationService] ===== CONSULTATION COMPLETION HANDLED SUCCESSFULLY =====");
        } catch (Exception e) {
            System.err.println("[ConsultationService] Error handling consultation completion: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - we don't want to rollback the consultation update if notifications fail
        }
    }
}
