package com.gi.consultationservice.dto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.gi.consultationservice.enums.ConsultationType;

class ConsultationSummaryDTOTest {

    @Test
    void testBuilder() {
        OffsetDateTime dateConsultation = OffsetDateTime.now();
        ZonedDateTime archivedAt = ZonedDateTime.now();

        ConsultationSummaryDTO dto = ConsultationSummaryDTO.builder()
                .id(1L)
                .dateConsultation(dateConsultation)
                .type(ConsultationType.CONSULTATION)
                .diagnostic("Grippe saisonnière")
                .traitement("Repos et hydratation")
                .observations("À revoir dans une semaine")
                .archived(false)
                .archivedAt(archivedAt)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(dto.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(dto.getDiagnostic()).isEqualTo("Grippe saisonnière");
        assertThat(dto.getTraitement()).isEqualTo("Repos et hydratation");
        assertThat(dto.getObservations()).isEqualTo("À revoir dans une semaine");
        assertThat(dto.getArchived()).isFalse();
        assertThat(dto.getArchivedAt()).isEqualTo(archivedAt);
    }

    @Test
    void testSetters() {
        ConsultationSummaryDTO dto = new ConsultationSummaryDTO();
        OffsetDateTime dateConsultation = OffsetDateTime.now();

        dto.setId(2L);
        dto.setDateConsultation(dateConsultation);
        dto.setType(ConsultationType.CONTROLE);
        dto.setDiagnostic("Contrôle satisfaisant");
        dto.setArchived(true);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(dto.getType()).isEqualTo(ConsultationType.CONTROLE);
        assertThat(dto.getDiagnostic()).isEqualTo("Contrôle satisfaisant");
        assertThat(dto.getArchived()).isTrue();
    }

    @Test
    void testNoArgsConstructor() {
        ConsultationSummaryDTO dto = new ConsultationSummaryDTO();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getType()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime archivedAt = ZonedDateTime.now();

        ConsultationSummaryDTO dto = new ConsultationSummaryDTO(
                1L, dateConsultation, ConsultationType.CONSULTATION,
                "Diagnostic", "Traitement", "Observations",
                false, archivedAt
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(dto.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(dto.getDiagnostic()).isEqualTo("Diagnostic");
        assertThat(dto.getTraitement()).isEqualTo("Traitement");
        assertThat(dto.getObservations()).isEqualTo("Observations");
    }

    @Test
    void testEqualsAndHashCode() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        ConsultationSummaryDTO dto1 = ConsultationSummaryDTO.builder()
                .id(1L)
                .dateConsultation(dateConsultation)
                .type(ConsultationType.CONSULTATION)
                .build();

        ConsultationSummaryDTO dto2 = ConsultationSummaryDTO.builder()
                .id(1L)
                .dateConsultation(dateConsultation)
                .type(ConsultationType.CONSULTATION)
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testToString() {
        ConsultationSummaryDTO dto = ConsultationSummaryDTO.builder()
                .id(1L)
                .type(ConsultationType.CONTROLE)
                .diagnostic("Test")
                .build();

        String toString = dto.toString();
        assertThat(toString).contains("1");
        assertThat(toString).contains("CONTROLE");
        assertThat(toString).contains("Test");
    }
}
