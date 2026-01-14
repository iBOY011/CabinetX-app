package com.gi.patientservice.enums;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class EventTypeTest {

    @Test
    void values_containsAllExpectedValues() {
        assertThat(EventType.values()).containsExactly(
                EventType.CREATED,
                EventType.UPDATED,
                EventType.DELETED
        );
    }

    @Test
    void valueOf_returnsCorrectEnum() {
        assertThat(EventType.valueOf("CREATED")).isEqualTo(EventType.CREATED);
        assertThat(EventType.valueOf("UPDATED")).isEqualTo(EventType.UPDATED);
        assertThat(EventType.valueOf("DELETED")).isEqualTo(EventType.DELETED);
    }

    @Test
    void name_returnsString() {
        assertThat(EventType.CREATED.name()).isEqualTo("CREATED");
        assertThat(EventType.UPDATED.name()).isEqualTo("UPDATED");
        assertThat(EventType.DELETED.name()).isEqualTo("DELETED");
    }

    @Test
    void ordinal_returnsCorrectIndex() {
        assertThat(EventType.CREATED.ordinal()).isEqualTo(0);
        assertThat(EventType.UPDATED.ordinal()).isEqualTo(1);
        assertThat(EventType.DELETED.ordinal()).isEqualTo(2);
    }
}
