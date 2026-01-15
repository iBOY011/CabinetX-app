package com.gi.consultationservice.web;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.dto.MedecinWeeklyStatsDTO;
import com.gi.consultationservice.enums.ConsultationType;
import com.gi.consultationservice.service.ConsultationService;

@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

    @Mock
    private ConsultationService consultationService;

    @InjectMocks
    private ConsultationController consultationController;

    private ConsultationDTO sampleDTO;
    private ConsultationSummaryDTO sampleSummary;

    @BeforeEach
    void setUp() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);

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

        sampleSummary = ConsultationSummaryDTO.builder()
                .id(1L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .diagnostic("Grippe")
                .traitement("Paracétamol")
                .build();
    }

    @Test
    void creerConsultation_shouldReturnCreatedConsultation() {
        when(consultationService.creerConsultation(any(ConsultationDTO.class))).thenReturn(sampleDTO);

        ConsultationDTO result = consultationController.creerConsultation(sampleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPatientId()).isEqualTo(10L);
        verify(consultationService).creerConsultation(any(ConsultationDTO.class));
    }

    @Test
    void modifierConsultation_shouldReturnUpdatedConsultation() {
        when(consultationService.modifierConsultation(eq(1L), any(ConsultationDTO.class))).thenReturn(sampleDTO);

        ConsultationDTO result = consultationController.modifierConsultation(1L, sampleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(consultationService).modifierConsultation(eq(1L), any(ConsultationDTO.class));
    }

    @Test
    void supprimerConsultation_shouldCallService() {
        doNothing().when(consultationService).supprimerConsultation(1L);

        consultationController.supprimerConsultation(1L);

        verify(consultationService).supprimerConsultation(1L);
    }

    @Test
    void trouverParId_shouldReturnConsultation() {
        when(consultationService.trouverParId(1L)).thenReturn(sampleDTO);

        ConsultationDTO result = consultationController.trouverParId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void trouverParRendezVous_shouldReturnConsultation() {
        when(consultationService.trouverParRendezVous(100L)).thenReturn(sampleDTO);

        ConsultationDTO result = consultationController.trouverParRendezVous(100L);

        assertThat(result).isNotNull();
        assertThat(result.getRendezVousId()).isEqualTo(100L);
    }

    @Test
    void listerToutes_shouldReturnAllConsultations() {
        List<ConsultationDTO> consultations = Arrays.asList(sampleDTO);
        when(consultationService.listerToutes()).thenReturn(consultations);

        List<ConsultationDTO> result = consultationController.listerToutes();

        assertThat(result).hasSize(1);
        verify(consultationService).listerToutes();
    }

    @Test
    void historiquePatient_shouldReturnPatientHistory() {
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        when(consultationService.listerParPatient(10L)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationController.historiquePatient(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDiagnostic()).isEqualTo("Grippe");
    }

    @Test
    void consultationsJour_shouldReturnDailyConsultations() {
        LocalDate date = LocalDate.of(2026, 1, 15);
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        when(consultationService.listerParMedecinEtJour(20L, date)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationController.consultationsJour(20L, date);

        assertThat(result).hasSize(1);
        verify(consultationService).listerParMedecinEtJour(20L, date);
    }

    @Test
    void consultationsPeriode_shouldReturnPeriodConsultations() {
        OffsetDateTime debut = OffsetDateTime.of(2026, 1, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fin = OffsetDateTime.of(2026, 1, 15, 23, 59, 59, 0, ZoneOffset.UTC);
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        when(consultationService.listerParMedecinEtPeriode(20L, debut, fin)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationController.consultationsPeriode(20L, debut, fin);

        assertThat(result).hasSize(1);
        verify(consultationService).listerParMedecinEtPeriode(20L, debut, fin);
    }

    @Test
    void consultationsRecents_shouldReturnRecentConsultations() {
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        when(consultationService.listerRecentsParMedecin(20L, 5)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationController.consultationsRecents(20L, 5);

        assertThat(result).hasSize(1);
        verify(consultationService).listerRecentsParMedecin(20L, 5);
    }

    @Test
    void consultationsRecents_shouldUseDefaultLimitWhenNegative() {
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        when(consultationService.listerRecentsParMedecin(20L, 5)).thenReturn(summaries);

        List<ConsultationSummaryDTO> result = consultationController.consultationsRecents(20L, -1);

        assertThat(result).hasSize(1);
        verify(consultationService).listerRecentsParMedecin(20L, 5);
    }

    @Test
    void consultationsPaged_shouldReturnPagedConsultations() {
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        Page<ConsultationSummaryDTO> page = new PageImpl<>(summaries);
        when(consultationService.listerParMedecinPaged(
                eq(20L), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        Page<ConsultationSummaryDTO> result = consultationController.consultationsPaged(
                20L, 0, 10, "dateConsultation,DESC", null, null, null);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void consultationsPaged_withFilters_shouldApplyFilters() {
        List<ConsultationSummaryDTO> summaries = Arrays.asList(sampleSummary);
        Page<ConsultationSummaryDTO> page = new PageImpl<>(summaries);
        when(consultationService.listerParMedecinPaged(
                eq(20L), eq(10L), eq(false), eq(ConsultationType.CONSULTATION), any(Pageable.class)))
                .thenReturn(page);

        Page<ConsultationSummaryDTO> result = consultationController.consultationsPaged(
                20L, 0, 10, "dateConsultation,DESC", 10L, false, ConsultationType.CONSULTATION);

        assertThat(result.getContent()).hasSize(1);
        verify(consultationService).listerParMedecinPaged(
                eq(20L), eq(10L), eq(false), eq(ConsultationType.CONSULTATION), any(Pageable.class));
    }

    @Test
    void statsMedecinWeekly_shouldReturnStats() {
        LocalDate startDate = LocalDate.of(2026, 1, 13);
        LocalDate endDate = LocalDate.of(2026, 1, 19);
        
        MedecinWeeklyStatsDTO stats = MedecinWeeklyStatsDTO.builder()
                .medecinId(20L)
                .cabinetId(30L)
                .consultationsCount(10L)
                .ordonnancesCount(8L)
                .presenceRate(100.0)
                .daily(Collections.emptyList())
                .build();

        when(consultationService.statsMedecinWeekly(20L, 30L, startDate, endDate)).thenReturn(stats);

        MedecinWeeklyStatsDTO result = consultationController.statsMedecinWeekly(20L, 30L, startDate, endDate);

        assertThat(result).isNotNull();
        assertThat(result.getMedecinId()).isEqualTo(20L);
        assertThat(result.getConsultationsCount()).isEqualTo(10L);
    }

    @Test
    void statsMedecinWeekly_withNullDates_shouldCallService() {
        MedecinWeeklyStatsDTO stats = MedecinWeeklyStatsDTO.builder()
                .medecinId(20L)
                .consultationsCount(5L)
                .daily(Collections.emptyList())
                .build();

        when(consultationService.statsMedecinWeekly(20L, null, null, null)).thenReturn(stats);

        MedecinWeeklyStatsDTO result = consultationController.statsMedecinWeekly(20L, null, null, null);

        assertThat(result).isNotNull();
        verify(consultationService).statsMedecinWeekly(20L, null, null, null);
    }

    @Test
    void listerToutes_shouldReturnEmptyListWhenNoConsultations() {
        when(consultationService.listerToutes()).thenReturn(Collections.emptyList());

        List<ConsultationDTO> result = consultationController.listerToutes();

        assertThat(result).isEmpty();
    }

    @Test
    void historiquePatient_shouldReturnEmptyListWhenNoHistory() {
        when(consultationService.listerParPatient(999L)).thenReturn(Collections.emptyList());

        List<ConsultationSummaryDTO> result = consultationController.historiquePatient(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void consultationsPaged_withDefaultSort_shouldParseCorrectly() {
        List<ConsultationSummaryDTO> summaries = Collections.emptyList();
        Page<ConsultationSummaryDTO> page = new PageImpl<>(summaries);
        when(consultationService.listerParMedecinPaged(
                anyLong(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        Page<ConsultationSummaryDTO> result = consultationController.consultationsPaged(
                20L, 0, 10, null, null, null, null);

        assertThat(result).isNotNull();
    }

    @Test
    void consultationsPaged_withAscSort_shouldParseCorrectly() {
        List<ConsultationSummaryDTO> summaries = Collections.emptyList();
        Page<ConsultationSummaryDTO> page = new PageImpl<>(summaries);
        when(consultationService.listerParMedecinPaged(
                anyLong(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        Page<ConsultationSummaryDTO> result = consultationController.consultationsPaged(
                20L, 0, 10, "id,ASC", null, null, null);

        assertThat(result).isNotNull();
    }
}
