package com.gi.consultationservice.dto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.gi.consultationservice.enums.ConsultationType;

class ConsultationDTOTest {

    @Test
    void testBuilder() {
        OffsetDateTime dateConsultation = OffsetDateTime.now();
        ZonedDateTime archivedAt = ZonedDateTime.now();

        ConsultationDTO dto = ConsultationDTO.builder()
                .id(1L)
                .rendezVousId(100L)
                .patientId(10L)
                .medecinId(20L)
                .cabinetId(30L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .examenClinique("Examen clinique")
                .examenSupplementaire("Radio")
                .diagnostic("Grippe")
                .traitement("Repos et paracétamol")
                .observations("Amélioration attendue")
                .archived(false)
                .archivedAt(archivedAt)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRendezVousId()).isEqualTo(100L);
        assertThat(dto.getPatientId()).isEqualTo(10L);
        assertThat(dto.getMedecinId()).isEqualTo(20L);
        assertThat(dto.getCabinetId()).isEqualTo(30L);
        assertThat(dto.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(dto.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(dto.getExamenClinique()).isEqualTo("Examen clinique");
        assertThat(dto.getExamenSupplementaire()).isEqualTo("Radio");
        assertThat(dto.getDiagnostic()).isEqualTo("Grippe");
        assertThat(dto.getTraitement()).isEqualTo("Repos et paracétamol");
        assertThat(dto.getObservations()).isEqualTo("Amélioration attendue");
        assertThat(dto.getArchived()).isFalse();
        assertThat(dto.getArchivedAt()).isEqualTo(archivedAt);
    }

    @Test
    void testSetters() {
        ConsultationDTO dto = new ConsultationDTO();
        OffsetDateTime dateConsultation = OffsetDateTime.now();

        dto.setId(2L);
        dto.setRendezVousId(200L);
        dto.setPatientId(15L);
        dto.setMedecinId(25L);
        dto.setCabinetId(35L);
        dto.setType(ConsultationType.CONTROLE);
        dto.setDateConsultation(dateConsultation);
        dto.setDiagnostic("Contrôle positif");
        dto.setArchived(true);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getRendezVousId()).isEqualTo(200L);
        assertThat(dto.getPatientId()).isEqualTo(15L);
        assertThat(dto.getMedecinId()).isEqualTo(25L);
        assertThat(dto.getCabinetId()).isEqualTo(35L);
        assertThat(dto.getType()).isEqualTo(ConsultationType.CONTROLE);
        assertThat(dto.getArchived()).isTrue();
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        ConsultationDTO dto1 = ConsultationDTO.builder()
                .id(1L)
                .patientId(10L)
                .medecinId(20L)
                .dateConsultation(dateConsultation)
                .type(ConsultationType.CONSULTATION)
                .build();

        ConsultationDTO dto2 = ConsultationDTO.builder()
                .id(1L)
                .patientId(10L)
                .medecinId(20L)
                .dateConsultation(dateConsultation)
                .type(ConsultationType.CONSULTATION)
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        ConsultationDTO dto1 = ConsultationDTO.builder()
                .id(1L)
                .patientId(10L)
                .build();

        ConsultationDTO dto2 = ConsultationDTO.builder()
                .id(2L)
                .patientId(20L)
                .build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        ConsultationDTO dto = ConsultationDTO.builder()
                .id(1L)
                .patientId(10L)
                .diagnostic("Test diagnostic")
                .build();

        String toString = dto.toString();
        assertThat(toString).contains("1");
        assertThat(toString).contains("10");
        assertThat(toString).contains("Test diagnostic");
    }

    @Test
    void testNoArgsConstructor() {
        ConsultationDTO dto = new ConsultationDTO();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getPatientId()).isNull();
        assertThat(dto.getType()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        OffsetDateTime dateConsultation = OffsetDateTime.now();
        ZonedDateTime archivedAt = ZonedDateTime.now();

        ConsultationDTO dto = new ConsultationDTO(
                1L, 100L, 10L, 20L, 30L,
                ConsultationType.CONSULTATION,
                dateConsultation,
                "Examen", "Supp", "Diag", "Trait", "Obs",
                false, archivedAt
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRendezVousId()).isEqualTo(100L);
        assertThat(dto.getPatientId()).isEqualTo(10L);
        assertThat(dto.getMedecinId()).isEqualTo(20L);
        assertThat(dto.getCabinetId()).isEqualTo(30L);
    }
}
