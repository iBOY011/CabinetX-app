package com.gi.prescriptionservice.messaging;

import java.time.LocalDate;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.gi.prescriptionservice.repository.PrescriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrdonnanceCountListener {

    private static final String RESPONSE_TOPIC = "ordonnance-count-response";

    private final PrescriptionRepository prescriptionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "ordonnance-count-request", groupId = "prescription-service-ordonnance")
    public void handleRequest(@Payload OrdonnanceCountRequest request) {
        if (request == null || request.getCorrelationId() == null || request.getMedecinId() == null
                || request.getStart() == null || request.getEnd() == null) {
            return;
        }

        LocalDate startDate = request.getStart().toLocalDate();
        LocalDate endDate = request.getEnd().toLocalDate();

        long count;
        if (request.getCabinetId() != null) {
            count = prescriptionRepository.countByDoctorIdAndClinicIdAndPrescriptionDateBetween(
                    request.getMedecinId(), request.getCabinetId(), startDate, endDate);
        } else {
            count = prescriptionRepository.countByDoctorIdAndPrescriptionDateBetween(
                    request.getMedecinId(), startDate, endDate);
        }

        OrdonnanceCountResponse response = new OrdonnanceCountResponse(request.getCorrelationId(), count);
        kafkaTemplate.send(RESPONSE_TOPIC, request.getCorrelationId(), response);
    }
}
