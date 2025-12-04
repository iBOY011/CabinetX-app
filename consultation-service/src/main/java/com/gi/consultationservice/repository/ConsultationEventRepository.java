package com.gi.consultationservice.repository;

import com.gi.consultationservice.entities.ConsultationCreatedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationEventRepository extends JpaRepository<ConsultationCreatedEvent, Long> {
}
