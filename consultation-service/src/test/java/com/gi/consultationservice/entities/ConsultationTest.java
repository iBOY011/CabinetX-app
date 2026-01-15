package com.gi.consultationservice.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.gi.consultationservice.enums.ConsultationType;

class ConsultationTest {

    @Test
    void testConsultationBuilder() {
        OffsetDateTime dateConsultation = OffsetDateTime.now();
        ZonedDateTime archivedAt = ZonedDateTime.now();

        Consultation consultation = Consultation.builder()
                .id(1L)
                .rendezVousId(100L)
                .patientId(10L)
                .medecinId(20L)
                .cabinetId(30L)
                .type(ConsultationType.CONSULTATION)
                .dateConsultation(dateConsultation)
                .examenClinique("Examen clinique détaillé")
                .examenSupplementaire("Radio thorax")
                .diagnostic("Bronchite aiguë")
                .traitement("Antibiotiques 7 jours")
                .observations("Patient en bonne voie de guérison")
                .archived(true)
                .archivedAt(archivedAt)
                .build();

        assertThat(consultation.getId()).isEqualTo(1L);
        assertThat(consultation.getRendezVousId()).isEqualTo(100L);
        assertThat(consultation.getPatientId()).isEqualTo(10L);
        assertThat(consultation.getMedecinId()).isEqualTo(20L);
        assertThat(consultation.getCabinetId()).isEqualTo(30L);
        assertThat(consultation.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(consultation.getDateConsultation()).isEqualTo(dateConsultation);
        assertThat(consultation.getExamenClinique()).isEqualTo("Examen clinique détaillé");
        assertThat(consultation.getExamenSupplementaire()).isEqualTo("Radio thorax");
        assertThat(consultation.getDiagnostic()).isEqualTo("Bronchite aiguë");
        assertThat(consultation.getTraitement()).isEqualTo("Antibiotiques 7 jours");
        assertThat(consultation.getObservations()).isEqualTo("Patient en bonne voie de guérison");
        assertThat(consultation.getArchived()).isTrue();
        assertThat(consultation.getArchivedAt()).isEqualTo(archivedAt);
    }

    @Test
    void testConsultationSetters() {
        Consultation consultation = new Consultation();
        OffsetDateTime dateConsultation = OffsetDateTime.now();
        ZonedDateTime archivedAt = ZonedDateTime.now();

        consultation.setId(2L);
        consultation.setRendezVousId(200L);
        consultation.setPatientId(15L);
        consultation.setMedecinId(25L);
        consultation.setCabinetId(35L);
        consultation.setType(ConsultationType.CONTROLE);
        consultation.setDateConsultation(dateConsultation);
        consultation.setExamenClinique("Examen de contrôle");
        consultation.setDiagnostic("Guérison confirmée");
        consultation.setTraitement("Arrêt du traitement");
        consultation.setArchived(false);
        consultation.setArchivedAt(archivedAt);

        assertThat(consultation.getId()).isEqualTo(2L);
        assertThat(consultation.getRendezVousId()).isEqualTo(200L);
        assertThat(consultation.getPatientId()).isEqualTo(15L);
        assertThat(consultation.getMedecinId()).isEqualTo(25L);
        assertThat(consultation.getCabinetId()).isEqualTo(35L);
        assertThat(consultation.getType()).isEqualTo(ConsultationType.CONTROLE);
        assertThat(consultation.getArchived()).isFalse();
    }

    @Test
    void testConsultationNoArgsConstructor() {
        Consultation consultation = new Consultation();
        assertThat(consultation).isNotNull();
        assertThat(consultation.getId()).isNull();
        assertThat(consultation.getRendezVousId()).isNull();
    }

    @Test
    void testConsultationAllArgsConstructor() {
        OffsetDateTime dateConsultation = OffsetDateTime.of(2026, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC);
        ZonedDateTime archivedAt = ZonedDateTime.now();

        Consultation consultation = new Consultation(
                1L, 100L, 10L, 20L, 30L,
                ConsultationType.CONSULTATION,
                dateConsultation,
                "Examen", "Supp", "Diag", "Trait", "Obs",
                false, archivedAt
        );

        assertThat(consultation.getId()).isEqualTo(1L);
        assertThat(consultation.getRendezVousId()).isEqualTo(100L);
        assertThat(consultation.getPatientId()).isEqualTo(10L);
        assertThat(consultation.getMedecinId()).isEqualTo(20L);
        assertThat(consultation.getCabinetId()).isEqualTo(30L);
        assertThat(consultation.getType()).isEqualTo(ConsultationType.CONSULTATION);
        assertThat(consultation.getDateConsultation()).isEqualTo(dateConsultation);
    }

    @Test
    void testConsultationToString() {
        Consultation consultation = Consultation.builder()
                .id(1L)
                .patientId(10L)
                .type(ConsultationType.CONSULTATION)
                .build();

        String toString = consultation.toString();
        assertThat(toString).contains("1");
        assertThat(toString).contains("10");
        assertThat(toString).contains("CONSULTATION");
    }

    @Test
    void testConsultationDefaultArchivedValue() {
        Consultation consultation = Consultation.builder()
                .id(1L)
                .build();
        
        // Le builder ne définit pas la valeur par défaut, c'est JPA qui le fait
        // Donc ici archived sera null avec le builder
        assertThat(consultation.getArchived()).isNull();
        
        // Mais avec new, la valeur par défaut est false
        Consultation consultation2 = new Consultation();
        assertThat(consultation2.getArchived()).isFalse();
    }
}
