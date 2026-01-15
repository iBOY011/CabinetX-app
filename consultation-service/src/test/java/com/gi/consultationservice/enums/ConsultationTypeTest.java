package com.gi.consultationservice.enums;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ConsultationTypeTest {

    @Test
    void testConsultationTypeValues() {
        ConsultationType[] values = ConsultationType.values();
        assertThat(values).hasSize(2);
        assertThat(values).containsExactly(
                ConsultationType.CONSULTATION,
                ConsultationType.CONTROLE
        );
    }

    @Test
    void testConsultationTypeValueOf() {
        assertThat(ConsultationType.valueOf("CONSULTATION")).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(ConsultationType.valueOf("CONTROLE")).isEqualTo(ConsultationType.CONTROLE);
    }

    @Test
    void testConsultationTypeName() {
        assertThat(ConsultationType.CONSULTATION.name()).isEqualTo("CONSULTATION");
        assertThat(ConsultationType.CONTROLE.name()).isEqualTo("CONTROLE");
    }

    @Test
    void testConsultationTypeOrdinal() {
        assertThat(ConsultationType.CONSULTATION.ordinal()).isZero();
        assertThat(ConsultationType.CONTROLE.ordinal()).isEqualTo(1);
    }
}
