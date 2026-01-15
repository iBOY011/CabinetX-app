package com.gi.consultationservice.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ConsultationCreatedEventTest {

    @Test
    void testConsultationCreatedEventBuilder() {
        OffsetDateTime dateConsultation = OffsetDateTime.now();

        ConsultationCreatedEvent event = ConsultationCreatedEvent.builder()
                .id(1L)
                .consultationId(100L)
                .patientId(10L)
                .medecinId(20L)
                .rendezVousId(50L)
                .dateConsultation(dateConsultation)
                .build();

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getConsultationId()).isEqualTo(100L);
        assertThat(event.getPatientId()).isEqualTo(10L);
        assertThat(event.getMedecinId()).isEqualTo(20L);
        assertThat(event.getRendezVousId()).isEqualTo(50L);
        assertThat(event.getDateConsultation()).isEqualTo(dateConsultation);
    }

    @Test
    void testConsultationCreatedEventSetters() {
        ConsultationCreatedEvent event = new ConsultationCreatedEvent();
        OffsetDateTime dateConsultation = OffsetDateTime.now();

        event.setId(2L);
        event.setConsultationId(200L);
        event.setPatientId(15L);
        event.setMedecinId(25L);
        event.setRendezVousId(55L);
        event.setDateConsultation(dateConsultation);

        assertThat(event.getId()).isEqualTo(2L);
        assertThat(event.getConsultationId()).isEqualTo(200L);
        assertThat(event.getPatientId()).isEqualTo(15L);
        assertThat(event.getMedecinId()).isEqualTo(25L);
        assertThat(event.getRendezVousId()).isEqualTo(55L);
        assertThat(event.getDateConsultation()).isEqualTo(dateConsultation);
    }

    @Test
    void testConsultationCreatedEventNoArgsConstructor() {
        ConsultationCreatedEvent event = new ConsultationCreatedEvent();
        assertThat(event).isNotNull();
        assertThat(event.getId()).isNull();
        assertThat(event.getConsultationId()).isNull();
    }

    @Test
    void testConsultationCreatedEventAllArgsConstructor() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 14, 0, 0, 0, ZoneOffset.UTC);

        ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                1L, 100L, 10L, 20L, 50L, dateConsultation
        );

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getConsultationId()).isEqualTo(100L);
        assertThat(event.getPatientId()).isEqualTo(10L);
        assertThat(event.getMedecinId()).isEqualTo(20L);
        assertThat(event.getRendezVousId()).isEqualTo(50L);
        assertThat(event.getDateConsultation()).isEqualTo(dateConsultation);
    }

    @Test
    void testConsultationCreatedEventToString() {
        ConsultationCreatedEvent event = ConsultationCreatedEvent.builder()
                .id(1L)
                .consultationId(100L)
                .patientId(10L)
                .build();

        String toString = event.toString();
        assertThat(toString).contains("1");
        assertThat(toString).contains("100");
        assertThat(toString).contains("10");
    }
}
