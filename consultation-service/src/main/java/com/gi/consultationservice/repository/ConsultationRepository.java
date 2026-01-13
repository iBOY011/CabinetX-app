package com.gi.consultationservice.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gi.consultationservice.entities.Consultation;

public interface ConsultationRepository extends JpaRepository<Consultation, Long>, JpaSpecificationExecutor<Consultation> {

    List<Consultation> findByPatientIdOrderByDateConsultationDesc(Long patientId);

        List<Consultation> findByMedecinIdAndDateConsultationBetweenOrderByDateConsultationAsc(
            Long medecinId,
            OffsetDateTime debut,
            OffsetDateTime fin);

    Optional<Consultation> findByRendezVousId(Long rendezVousId);
}
