package com.gi.consultationservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ConsultationCompletedEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationCompletedEventPublisher.class);
    private static final String TOPIC = "consultation-completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ConsultationCompletedEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void publish(ConsultationCompletedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event);
            LOGGER.info("Published ConsultationCompletedEvent for consultation ID: {}", event.getConsultationId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish ConsultationCompletedEvent for consultation ID: {}", event.getConsultationId(), e);
        }
    }
}