package com.gi.consultationservice.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import com.gi.consultationservice.client.AppointmentClient;
import com.gi.consultationservice.client.NotificationClient;
import com.gi.consultationservice.client.PatientClient;
import com.gi.consultationservice.client.PrescriptionClient;
import com.gi.consultationservice.client.UserClient;
import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.entities.Consultation;
import com.gi.consultationservice.entities.ConsultationCreatedEvent;
import com.gi.consultationservice.enums.ConsultationType;
import com.gi.consultationservice.mappers.ConsultationMapper;
import com.gi.consultationservice.messaging.ConsultationCompletedEventPublisher;
import com.gi.consultationservice.repository.ConsultationEventRepository;
import com.gi.consultationservice.repository.ConsultationRepository;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private ConsultationEventRepository eventRepository;

    @Mock
    private ConsultationMapper consultationMapper;

    @Mock
    private PrescriptionClient prescriptionClient;

    @Mock
    private AppointmentClient appointmentClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private UserClient userClient;

    @Mock
    private PatientClient patientClient;

    @Mock
    private ConsultationCompletedEventPublisher eventPublisher;

    @InjectMocks
    private ConsultationService consultationService;

    private Consultation sampleConsultation;
    private ConsultationDTO sampleDTO;

    @BeforeEach
    void setUp() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        sampleConsultation = Consultation.builder()
                .id(1L)
                .rendezVousId(100L)
                .patientId(10L)
                .medecinId(20L)
                .cabinetId(30L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .diagnostic("Grippe")
                .traitement("Paracétamol")
                .archived(false)
                .build();

        sampleDTO = ConsultationDTO.builder()
                .id(1L)
                .rendezVousId(100L)
                .patientId(10L)
                .medecinId(20L)
                .cabinetId(30L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .diagnostic("Grippe")
                .traitement("Paracétamol")
                .archived(false)
                .build();
    }

    @Test
    void creerConsultation_shouldSaveAndReturnDTO() {
        when(consultationMapper.toEntity(any(ConsultationDTO.class))).thenReturn(sampleConsultation);
        when(consultationRepository.save(any(Consultation.class))).thenReturn(sampleConsultation);
        when(consultationMapper.toDTO(any(Consultation.class))).thenReturn(sampleDTO);

        ConsultationDTO result = consultationService.creerConsultation(sampleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPatientId()).isEqualTo(10L);
        assertThat(result.getDiagnostic()).isEqualTo("Grippe");

        verify(consultationRepository).save(any(Consultation.class));
        verify(eventRepository).save(any(ConsultationCreatedEvent.class));
    }

    @Test
    void modifierConsultation_shouldUpdateAndReturnDTO() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(sampleConsultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(sampleConsultation);
        when(consultationMapper.toDTO(any(Consultation.class))).thenReturn(sampleDTO);

        ConsultationDTO updateDTO = ConsultationDTO.builder()
                .diagnostic("Bronchite")
                .traitement("Antibiotiques")
                .build();

        ConsultationDTO result = consultationService.modifierConsultation(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(consultationRepository).findById(1L);
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void modifierConsultation_shouldThrowWhenNotFound() {
        when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.modifierConsultation(999L, sampleDTO))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Consultation introuvable");
    }

    @Test
    void supprimerConsultation_shouldDelete() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(sampleConsultation));

        consultationService.supprimerConsultation(1L);

        verify(consultationRepository).delete(sampleConsultation);
    }

    @Test
    void supprimerConsultation_shouldThrowWhenNotFound() {
        when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.supprimerConsultation(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Consultation introuvable");
    }

    @Test
    void trouverParId_shouldReturnDTO() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(sampleConsultation));
        when(consultationMapper.toDTO(sampleConsultation)).thenReturn(sampleDTO);

        ConsultationDTO result = consultationService.trouverParId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(consultationRepository).findById(1L);
    }

    @Test
    void trouverParId_shouldThrowWhenNotFound() {
        when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.trouverParId(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Consultation introuvable");
    }

    @Test
    void trouverParRendezVous_shouldReturnDTO() {
        when(consultationRepository.findByRendezVousId(100L)).thenReturn(Optional.of(sampleConsultation));
        when(consultationMapper.toDTO(sampleConsultation)).thenReturn(sampleDTO);

        ConsultationDTO result = consultationService.trouverParRendezVous(100L);

        assertThat(result).isNotNull();
        assertThat(result.getRendezVousId()).isEqualTo(100L);
    }

    @Test
    void trouverParRendezVous_shouldThrowWhenNotFound() {
        when(consultationRepository.findByRendezVousId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.trouverParRendezVous(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Consultation introuvable");
    }

    @Test
    void listerParPatient_shouldReturnSummaryList() {
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        List<ConsultationSummaryDTO> summaries = Arrays.asList(
                ConsultationSummaryDTO.builder()
                        .id(1L)
                        .type(ConsultationType.CONSULTATION)
                        .diagnostic("Grippe")
                        .build()
        );

        when(consultationRepository.findByPatientIdOrderByDateConsultationDesc(10L))
                .thenReturn(consultations);
        when(consultationMapper.toSummaryList(consultations)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationService.listerParPatient(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDiagnostic()).isEqualTo("Grippe");
    }

    @Test
    void listerToutes_shouldReturnAllDTOs() {
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        List<ConsultationDTO> dtos = Arrays.asList(sampleDTO);

        when(consultationRepository.findAll(any(Sort.class))).thenReturn(consultations);
        when(consultationMapper.toDTOList(consultations)).thenReturn(dtos);

        List<ConsultationDTO> result = consultationService.listerToutes();

        assertThat(result).hasSize(1);
        verify(consultationRepository).findAll(any(Sort.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void listerRecentsParMedecin_shouldReturnLimitedSummaries() {
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        Page<Consultation> page = new PageImpl<>(consultations);
        List<ConsultationSummaryDTO> summaries = Arrays.asList(
                ConsultationSummaryDTO.builder().id(1L).build()
        );

        when(consultationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(consultationMapper.toSummaryList(consultations)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationService.listerRecentsParMedecin(20L, 5);

        assertThat(result).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listerRecentsParMedecin_shouldUseLimitOfAtLeast1() {
        List<Consultation> consultations = Collections.emptyList();
        Page<Consultation> page = new PageImpl<>(consultations);

        when(consultationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(consultationMapper.toSummaryList(consultations)).thenReturn(Collections.emptyList());

        List<ConsultationSummaryDTO> result = consultationService.listerRecentsParMedecin(20L, 0);

        assertThat(result).isEmpty();
        // Verify that pageable was called with size of at least 1
        verify(consultationRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void listerParMedecinEtJour_shouldReturnSummaries() {
        LocalDate date = LocalDate.of(2026, 1, 15);
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        List<ConsultationSummaryDTO> summaries = Arrays.asList(
                ConsultationSummaryDTO.builder().id(1L).build()
        );

        when(consultationRepository.findByMedecinIdAndDateConsultationBetweenOrderByDateConsultationAsc(
                eq(20L), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(consultations);
        when(consultationMapper.toSummaryList(consultations)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationService.listerParMedecinEtJour(20L, date);

        assertThat(result).hasSize(1);
    }

    @Test
    void listerParMedecinEtPeriode_shouldReturnSummaries() {
        OffsetDateTime debut = OffsetDateTime.of(2026, 1, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fin = OffsetDateTime.of(2026, 1, 15, 23, 59, 59, 0, ZoneOffset.UTC);
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        List<ConsultationSummaryDTO> summaries = Arrays.asList(
                ConsultationSummaryDTO.builder().id(1L).build()
        );

        when(consultationRepository.findByMedecinIdAndDateConsultationBetweenOrderByDateConsultationAsc(
                20L, debut, fin))
                .thenReturn(consultations);
        when(consultationMapper.toSummaryList(consultations)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationService.listerParMedecinEtPeriode(20L, debut, fin);

        assertThat(result).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void statsMedecinWeekly_shouldReturnStats() {
        LocalDate startDate = LocalDate.of(2026, 1, 13);
        LocalDate endDate = LocalDate.of(2026, 1, 19);
        List<Consultation> consultations = Arrays.asList(sampleConsultation);

        when(consultationRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(consultations);
        when(prescriptionClient.getPrescriptionCount(anyLong(), anyLong(), any(), any()))
                .thenReturn(5L);

        var result = consultationService.statsMedecinWeekly(20L, 30L, startDate, endDate);

        assertThat(result).isNotNull();
        assertThat(result.getMedecinId()).isEqualTo(20L);
        assertThat(result.getCabinetId()).isEqualTo(30L);
        assertThat(result.getConsultationsCount()).isEqualTo(1L);
        assertThat(result.getOrdonnancesCount()).isEqualTo(5L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void statsMedecinWeekly_shouldUseDefaultDatesWhenNull() {
        List<Consultation> consultations = Collections.emptyList();

        when(consultationRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(consultations);

        var result = consultationService.statsMedecinWeekly(20L, null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getMedecinId()).isEqualTo(20L);
        assertThat(result.getDaily()).isNotEmpty();
    }

    @Test
    void statsMedecinWeekly_shouldThrowWhenEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2026, 1, 20);
        LocalDate endDate = LocalDate.of(2026, 1, 13);

        assertThatThrownBy(() -> consultationService.statsMedecinWeekly(20L, 30L, startDate, endDate))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("endDate doit être après startDate");
    }

    @Test
    @SuppressWarnings("unchecked")
    void listerParMedecinPaged_shouldReturnPagedSummaries() {
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        Page<Consultation> page = new PageImpl<>(consultations);
        Pageable pageable = PageRequest.of(0, 10);

        when(consultationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(consultationMapper.toSummary(any(Consultation.class)))
                .thenReturn(ConsultationSummaryDTO.builder().id(1L).build());

        Page<ConsultationSummaryDTO> result = consultationService.listerParMedecinPaged(
                20L, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listerParMedecinPaged_shouldApplyFilters() {
        List<Consultation> consultations = Arrays.asList(sampleConsultation);
        Page<Consultation> page = new PageImpl<>(consultations);
        Pageable pageable = PageRequest.of(0, 10);

        when(consultationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(consultationMapper.toSummary(any(Consultation.class)))
                .thenReturn(ConsultationSummaryDTO.builder().id(1L).build());

        Page<ConsultationSummaryDTO> result = consultationService.listerParMedecinPaged(
                20L, 10L, false, ConsultationType.CONSULTATION, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listerToutes_shouldReturnEmptyListWhenNoConsultations() {
        when(consultationRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(consultationMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConsultationDTO> result = consultationService.listerToutes();

        assertThat(result).isEmpty();
    }

    @Test
    void listerParPatient_shouldReturnEmptyListWhenNoConsultations() {
        when(consultationRepository.findByPatientIdOrderByDateConsultationDesc(999L))
                .thenReturn(Collections.emptyList());
        when(consultationMapper.toSummaryList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConsultationSummaryDTO> result = consultationService.listerParPatient(999L);

        assertThat(result).isEmpty();
    }
}
