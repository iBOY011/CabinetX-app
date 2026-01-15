package com.gi.consultationservice.dto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class DailyStatDTOTest {

    @Test
    void testBuilder() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyStatDTO dto = DailyStatDTO.builder()
                .date(date)
                .consultations(5L)
                .ordonnances(3L)
                .presenceRate(85.5)
                .build();

        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getConsultations()).isEqualTo(5L);
        assertThat(dto.getOrdonnances()).isEqualTo(3L);
        assertThat(dto.getPresenceRate()).isEqualTo(85.5);
    }

    @Test
    void testValueImmutability() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyStatDTO dto = DailyStatDTO.builder()
                .date(date)
                .consultations(10L)
                .ordonnances(8L)
                .presenceRate(100.0)
                .build();

        // @Value crée des objets immuables - pas de setters
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getConsultations()).isEqualTo(10L);
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyStatDTO dto1 = DailyStatDTO.builder()
                .date(date)
                .consultations(5L)
                .ordonnances(3L)
                .presenceRate(85.5)
                .build();

        DailyStatDTO dto2 = DailyStatDTO.builder()
                .date(date)
                .consultations(5L)
                .ordonnances(3L)
                .presenceRate(85.5)
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyStatDTO dto1 = DailyStatDTO.builder()
                .date(date)
                .consultations(5L)
                .ordonnances(3L)
                .presenceRate(85.5)
                .build();

        DailyStatDTO dto2 = DailyStatDTO.builder()
                .date(date)
                .consultations(10L)  // Different value
                .ordonnances(3L)
                .presenceRate(85.5)
                .build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyStatDTO dto = DailyStatDTO.builder()
                .date(date)
                .consultations(5L)
                .ordonnances(3L)
                .presenceRate(85.5)
                .build();

        String toString = dto.toString();
        assertThat(toString).contains("2026-01-15");
        assertThat(toString).contains("5");
        assertThat(toString).contains("3");
        assertThat(toString).contains("85.5");
    }

    @Test
    void testZeroValues() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyStatDTO dto = DailyStatDTO.builder()
                .date(date)
                .consultations(0L)
                .ordonnances(0L)
                .presenceRate(0.0)
                .build();

        assertThat(dto.getConsultations()).isZero();
        assertThat(dto.getOrdonnances()).isZero();
        assertThat(dto.getPresenceRate()).isZero();
    }
}
