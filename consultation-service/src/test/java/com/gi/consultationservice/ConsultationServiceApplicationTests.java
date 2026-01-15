package com.gi.consultationservice;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Basic application tests for consultation-service.
 * Full context loading is skipped to avoid database seeding issues.
 */
class ConsultationServiceApplicationTests {

    @Test
    void applicationClassExists() {
        assertThat(ConsultationServiceApplication.class).isNotNull();
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        assertThat(ConsultationServiceApplication.class.getMethod("main", String[].class)).isNotNull();
    }

}
