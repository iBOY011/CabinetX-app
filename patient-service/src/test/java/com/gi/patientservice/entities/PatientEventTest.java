package com.gi.patientservice.entities;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.gi.patientservice.enums.EventType;

class PatientEventTest {

    @Test
    void builder_createsEventWithAllFields() {
        PatientEvent event = PatientEvent.builder()
                .id(1L)
                .patientId(10L)
                .type(EventType.CREATED)
                .build();

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getPatientId()).isEqualTo(10L);
        assertThat(event.getType()).isEqualTo(EventType.CREATED);
    }

    @Test
    void setters_modifyFields() {
        PatientEvent event = new PatientEvent();
        event.setId(5L);
        event.setPatientId(20L);
        event.setType(EventType.UPDATED);

        assertThat(event.getId()).isEqualTo(5L);
        assertThat(event.getPatientId()).isEqualTo(20L);
        assertThat(event.getType()).isEqualTo(EventType.UPDATED);
    }

    @Test
    void noArgsConstructor_createsEmptyEvent() {
        PatientEvent event = new PatientEvent();

        assertThat(event.getId()).isNull();
        assertThat(event.getPatientId()).isNull();
        assertThat(event.getType()).isNull();
        assertThat(event.getOccurredAt()).isNull();
    }

    @Test
    void eventType_coversAllValues() {
        assertThat(EventType.values()).containsExactly(
                EventType.CREATED,
                EventType.UPDATED,
                EventType.DELETED
        );
    }
}
