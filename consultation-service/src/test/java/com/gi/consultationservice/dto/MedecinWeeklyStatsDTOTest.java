package com.gi.consultationservice.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class MedecinWeeklyStatsDTOTest {

    @Test
    void testBuilder() {
        OffsetDateTime start = OffsetDateTime.of(2026, 1, 13, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(2026, 1, 19, 23, 59, 59, 0, ZoneOffset.UTC);
        
        List<DailyStatDTO> daily = Arrays.asList(
                DailyStatDTO.builder()
                        .date(LocalDate.of(2026, 1, 13))
                        .consultations(5L)
                        .ordonnances(3L)
                        .presenceRate(100.0)
                        .build(),
                DailyStatDTO.builder()
                        .date(LocalDate.of(2026, 1, 14))
                        .consultations(8L)
                        .ordonnances(6L)
                        .presenceRate(100.0)
                        .build()
        );

        MedecinWeeklyStatsDTO dto = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(13L)
                .ordonnancesCount(9L)
                .presenceRate(100.0)
                .daily(daily)
                .build();

        assertThat(dto.getMedecinId()).isEqualTo(1L);
        assertThat(dto.getCabinetId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getConsultationsCount()).isEqualTo(13L);
        assertThat(dto.getOrdonnancesCount()).isEqualTo(9L);
        assertThat(dto.getPresenceRate()).isEqualTo(100.0);
        assertThat(dto.getDaily()).hasSize(2);
    }

    @Test
    void testValueImmutability() {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = start.plusDays(7);

        MedecinWeeklyStatsDTO dto = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(20L)
                .ordonnancesCount(15L)
                .presenceRate(95.5)
                .daily(Collections.emptyList())
                .build();

        // @Value crée des objets immuables
        assertThat(dto.getMedecinId()).isEqualTo(1L);
        assertThat(dto.getConsultationsCount()).isEqualTo(20L);
    }

    @Test
    void testEqualsAndHashCode() {
        OffsetDateTime start = OffsetDateTime.of(2026, 1, 13, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(2026, 1, 19, 23, 59, 59, 0, ZoneOffset.UTC);
        List<DailyStatDTO> daily = Collections.emptyList();

        MedecinWeeklyStatsDTO dto1 = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(13L)
                .ordonnancesCount(9L)
                .presenceRate(100.0)
                .daily(daily)
                .build();

        MedecinWeeklyStatsDTO dto2 = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(13L)
                .ordonnancesCount(9L)
                .presenceRate(100.0)
                .daily(daily)
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = start.plusDays(7);

        MedecinWeeklyStatsDTO dto1 = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(13L)
                .ordonnancesCount(9L)
                .presenceRate(100.0)
                .daily(Collections.emptyList())
                .build();

        MedecinWeeklyStatsDTO dto2 = MedecinWeeklyStatsDTO.builder()
                .medecinId(2L)  // Different medecinId
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(13L)
                .ordonnancesCount(9L)
                .presenceRate(100.0)
                .daily(Collections.emptyList())
                .build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        OffsetDateTime start = OffsetDateTime.of(2026, 1, 13, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(2026, 1, 19, 23, 59, 59, 0, ZoneOffset.UTC);

        MedecinWeeklyStatsDTO dto = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(10L)
                .start(start)
                .end(end)
                .consultationsCount(13L)
                .ordonnancesCount(9L)
                .presenceRate(100.0)
                .daily(Collections.emptyList())
                .build();

        String toString = dto.toString();
        assertThat(toString).contains("medecinId=1");
        assertThat(toString).contains("cabinetId=10");
        assertThat(toString).contains("consultationsCount=13");
    }

    @Test
    void testWithNullCabinetId() {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = start.plusDays(7);

        MedecinWeeklyStatsDTO dto = MedecinWeeklyStatsDTO.builder()
                .medecinId(1L)
                .cabinetId(null)
                .start(start)
                .end(end)
                .consultationsCount(10L)
                .ordonnancesCount(5L)
                .presenceRate(80.0)
                .daily(Collections.emptyList())
                .build();

        assertThat(dto.getMedecinId()).isEqualTo(1L);
        assertThat(dto.getCabinetId()).isNull();
    }
}
