package com.gi.consultationservice.repository;

import com.gi.consultationservice.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByPatientIdOrderByDateConsultationDesc(Long patientId);

    List<Consultation> findByMedecinIdAndDateConsultationBetweenOrderByDateConsultationAsc(
            Long medecinId,
            LocalDateTime debut,
            LocalDateTime fin);

    Optional<Consultation> findByRendezVousId(Long rendezVousId);
}
