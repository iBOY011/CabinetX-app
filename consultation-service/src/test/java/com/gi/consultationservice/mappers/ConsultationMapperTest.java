package com.gi.consultationservice.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.entities.Consultation;
import com.gi.consultationservice.enums.ConsultationType;

class ConsultationMapperTest {

    private ConsultationMapper consultationMapper;

    @BeforeEach
    void setUp() {
        consultationMapper = new ConsultationMapper();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime archivedAt = ZonedDateTime.now();

        ConsultationDTO dto = ConsultationDTO.builder()
                .id(1L)
                .rendezVousId(100L)
                .patientId(10L)
                .medecinId(20L)
                .cabinetId(30L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .examenClinique("Examen clinique complet")
                .examenSupplementaire("Radio thorax")
                .diagnostic("Bronchite")
                .traitement("Antibiotiques")
                .observations("À revoir dans 7 jours")
                .archived(false)
                .archivedAt(archivedAt)
                .build();

        Consultation entity = consultationMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getRendezVousId()).isEqualTo(100L);
        assertThat(entity.getPatientId()).isEqualTo(10L);
        assertThat(entity.getMedecinId()).isEqualTo(20L);
        assertThat(entity.getCabinetId()).isEqualTo(30L);
        assertThat(entity.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(entity.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(entity.getExamenClinique()).isEqualTo("Examen clinique complet");
        assertThat(entity.getExamenSupplementaire()).isEqualTo("Radio thorax");
        assertThat(entity.getDiagnostic()).isEqualTo("Bronchite");
        assertThat(entity.getTraitement()).isEqualTo("Antibiotiques");
        assertThat(entity.getObservations()).isEqualTo("À revoir dans 7 jours");
        assertThat(entity.getArchived()).isFalse();
        assertThat(entity.getArchivedAt()).isEqualTo(archivedAt);
    }

    @Test
    void toDTO_shouldMapAllFields() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 14, 30, 0, 0, ZoneOffset.UTC);
        ZonedDateTime archivedAt = ZonedDateTime.now();

        Consultation entity = Consultation.builder()
                .id(2L)
                .rendezVousId(200L)
                .patientId(15L)
                .medecinId(25L)
                .cabinetId(35L)
                .type(ConsultationType.CONTROLE)
                .dateConsultation(dateConsultation)
                .examenClinique("Contrôle de routine")
                .examenSupplementaire("Analyse sang")
                .diagnostic("État stable")
                .traitement("Maintien traitement")
                .observations("Prochain contrôle dans 3 mois")
                .archived(true)
                .archivedAt(archivedAt)
                .build();

        ConsultationDTO dto = consultationMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getRendezVousId()).isEqualTo(200L);
        assertThat(dto.getPatientId()).isEqualTo(15L);
        assertThat(dto.getMedecinId()).isEqualTo(25L);
        assertThat(dto.getCabinetId()).isEqualTo(35L);
        assertThat(dto.getType()).isEqualTo(ConsultationType.CONTROLE);
        assertThat(dto.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(dto.getExamenClinique()).isEqualTo("Contrôle de routine");
        assertThat(dto.getExamenSupplementaire()).isEqualTo("Analyse sang");
        assertThat(dto.getDiagnostic()).isEqualTo("État stable");
        assertThat(dto.getTraitement()).isEqualTo("Maintien traitement");
        assertThat(dto.getObservations()).isEqualTo("Prochain contrôle dans 3 mois");
        assertThat(dto.getArchived()).isTrue();
        assertThat(dto.getArchivedAt()).isEqualTo(archivedAt);
    }

    @Test
    void toSummary_shouldMapRelevantFields() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 9, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime archivedAt = ZonedDateTime.now();

        Consultation entity = Consultation.builder()
                .id(3L)
                .rendezVousId(300L)
                .patientId(30L)
                .medecinId(40L)
                .cabinetId(50L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .examenClinique("Examen")
                .diagnostic("Grippe")
                .traitement("Paracétamol")
                .observations("Repos recommandé")
                .archived(false)
                .archivedAt(archivedAt)
                .build();

        ConsultationSummaryDTO summary = consultationMapper.toSummary(entity);

        assertThat(summary).isNotNull();
        assertThat(summary.getId()).isEqualTo(3L);
        assertThat(summary.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(summary.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(summary.getDiagnostic()).isEqualTo("Grippe");
        assertThat(summary.getTraitement()).isEqualTo("Paracétamol");
        assertThat(summary.getObservations()).isEqualTo("Repos recommandé");
        assertThat(summary.getArchived()).isFalse();
        assertThat(summary.getArchivedAt()).isEqualTo(archivedAt);
    }

    @Test
    void toEntity_shouldReturnNullForNullDTO() {
        Consultation entity = consultationMapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toDTO_shouldReturnNullForNullEntity() {
        ConsultationDTO dto = consultationMapper.toDTO(null);
        assertThat(dto).isNull();
    }

    @Test
    void toSummary_shouldReturnNullForNullEntity() {
        ConsultationSummaryDTO summary = consultationMapper.toSummary(null);
        assertThat(summary).isNull();
    }

    @Test
    void toDTOList_shouldMapAllElements() {
        OffsetDateTime date1 = OffsetDateTime.now();
        OffsetDateTime date2 = OffsetDateTime.now().minusDays(1);

        List<Consultation> consultations = Arrays.asList(
                Consultation.builder()
                        .id(1L)
                        .patientId(10L)
                        .medecinId(20L)
                        .cabinetId(30L)
                        .rendezVousId(100L)
                        .type(ConsultationType.CONSULTATION)
                        .dateConsultation(date1)
                        .diagnostic("Diagnostic 1")
                        .build(),
                Consultation.builder()
                        .id(2L)
                        .patientId(11L)
                        .medecinId(21L)
                        .cabinetId(31L)
                        .rendezVousId(101L)
                        .type(ConsultationType.CONTROLE)
                        .dateConsultation(date2)
                        .diagnostic("Diagnostic 2")
                        .build()
        );

        List<ConsultationDTO> dtos = consultationMapper.toDTOList(consultations);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getId()).isEqualTo(1L);
        assertThat(dtos.get(0).getDiagnostic()).isEqualTo("Diagnostic 1");
        assertThat(dtos.get(1).getId()).isEqualTo(2L);
        assertThat(dtos.get(1).getDiagnostic()).isEqualTo("Diagnostic 2");
    }

    @Test
    void toDTOList_shouldReturnEmptyListForNullInput() {
        List<ConsultationDTO> dtos = consultationMapper.toDTOList(null);
        assertThat(dtos).isEmpty();
    }

    @Test
    void toDTOList_shouldReturnEmptyListForEmptyInput() {
        List<ConsultationDTO> dtos = consultationMapper.toDTOList(Collections.emptyList());
        assertThat(dtos).isEmpty();
    }

    @Test
    void toSummaryList_shouldMapAllElements() {
        OffsetDateTime date1 = OffsetDateTime.now();
        OffsetDateTime date2 = OffsetDateTime.now().minusDays(1);

        List<Consultation> consultations = Arrays.asList(
                Consultation.builder()
                        .id(1L)
                        .type(ConsultationType.CONSULTATION)
                        .dateConsultation(date1)
                        .diagnostic("Diagnostic 1")
                        .traitement("Traitement 1")
                        .build(),
                Consultation.builder()
                        .id(2L)
                        .type(ConsultationType.CONTROLE)
                        .dateConsultation(date2)
                        .diagnostic("Diagnostic 2")
                        .traitement("Traitement 2")
                        .build()
        );

        List<ConsultationSummaryDTO> summaries = consultationMapper.toSummaryList(consultations);

        assertThat(summaries).hasSize(2);
        assertThat(summaries.get(0).getId()).isEqualTo(1L);
        assertThat(summaries.get(0).getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(summaries.get(1).getId()).isEqualTo(2L);
        assertThat(summaries.get(1).getType()).isEqualTo(ConsultationType.CONTROLE);
    }

    @Test
    void toSummaryList_shouldReturnEmptyListForNullInput() {
        List<ConsultationSummaryDTO> summaries = consultationMapper.toSummaryList(null);
        assertThat(summaries).isEmpty();
    }

    @Test
    void toSummaryList_shouldReturnEmptyListForEmptyInput() {
        List<ConsultationSummaryDTO> summaries = consultationMapper.toSummaryList(Collections.emptyList());
        assertThat(summaries).isEmpty();
    }

    @Test
    void toEntity_shouldHandleNullOptionalFields() {
        OffsetDateTime dateConsultation = OffsetDateTime.now();

        ConsultationDTO dto = ConsultationDTO.builder()
                .id(1L)
                .rendezVousId(100L)
                .patientId(10L)
                .medecinId(20L)
                .cabinetId(30L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                // Optional fields left null
                .build();

        Consultation entity = consultationMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getExamenClinique()).isNull();
        assertThat(entity.getExamenSupplementaire()).isNull();
        assertThat(entity.getDiagnostic()).isNull();
        assertThat(entity.getTraitement()).isNull();
        assertThat(entity.getObservations()).isNull();
    }
}
