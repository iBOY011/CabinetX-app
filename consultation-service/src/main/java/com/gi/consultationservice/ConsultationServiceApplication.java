package com.gi.consultationservice;

import com.gi.consultationservice.entities.Consultation;
import com.gi.consultationservice.enums.ConsultationType;
import com.gi.consultationservice.repository.ConsultationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.OffsetDateTime;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class ConsultationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultationServiceApplication.class, args);
    }

    @Bean
    @SuppressWarnings("unused")
    CommandLineRunner seedConsultations(ConsultationRepository consultationRepository) {
        return args -> {
            if (consultationRepository.count() > 0) {
                return;
            }

            List<Consultation> initialConsultations = List.of(
                    Consultation.builder()
                            .rendezVousId(1001L)
                            .patientId(1L)
                            .medecinId(10L)
                            .cabinetId(1L)
                            .type(ConsultationType.CONSULTATION)
                            .dateConsultation(OffsetDateTime.now().minusDays(5))
                            .examenClinique("Examen général normal")
                            .diagnostic("Fatigue passagère")
                            .traitement("Repos et hydratation")
                            .observations("Suivi dans deux semaines")
                            .build(),
                    Consultation.builder()
                            .rendezVousId(1002L)
                            .patientId(2L)
                            .medecinId(11L)
                            .cabinetId(1L)
                            .type(ConsultationType.CONTROLE)
                            .dateConsultation(OffsetDateTime.now().minusDays(2))
                            .examenClinique("Pression artérielle stabilisée")
                            .diagnostic("Hypertension contrôlée")
                            .traitement("Poursuivre traitement actuel")
                            .observations("Contrôle dans un mois")
                            .build(),
                    Consultation.builder()
                            .rendezVousId(1003L)
                            .patientId(3L)
                            .medecinId(10L)
                            .cabinetId(2L)
                            .type(ConsultationType.CONSULTATION)
                            .dateConsultation(OffsetDateTime.now().minusDays(1))
                            .examenClinique("Douleurs abdominales")
                            .examenSupplementaire("Échographie prévue")
                            .diagnostic("Suspicion gastrite")
                            .traitement("IPP 20mg")
                            .observations("Réévaluation après examens")
                            .build()
            );

            consultationRepository.saveAll(initialConsultations);
        };
    }
}
