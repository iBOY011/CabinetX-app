package com.gi.billingservice.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gi.billingservice.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ConsultationCompletedEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationCompletedEventConsumer.class);

    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    public ConsultationCompletedEventConsumer(BillingService billingService, ObjectMapper objectMapper) {
        this.billingService = billingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "consultation-completed", groupId = "billing-service-consultation")
    public void handleConsultationCompleted(String message) {
        LOGGER.info("Received ConsultationCompletedEvent message: {}", message);
        
        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            LOGGER.error("Failed to parse JSON message: {}", message, e);
            return;
        }
        
        LOGGER.info("Parsed payload: {}", payload);

        try {
            // Extract data from the Map payload
            Long consultationId = extractLong(payload, "consultationId");
            Long patientId = extractLong(payload, "patientId");
            Long cabinetId = extractLong(payload, "cabinetId");
            Long medecinId = extractLong(payload, "medecinId");
            String traitement = (String) payload.get("traitement");

            // Calculate invoice amount
            BigDecimal amount = calculateInvoiceAmount(traitement);

            // Generate invoice for the completed consultation
            billingService.generateInvoiceFromConsultation(
                consultationId,
                patientId,
                cabinetId,
                medecinId,
                amount
            );

            LOGGER.info("Successfully processed consultation completion for consultation ID: {}", consultationId);

        } catch (Exception e) {
            LOGGER.error("Failed to process ConsultationCompletedEvent: {}", payload, e);
        }
    }

    private Long extractLong(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    /**
     * Calculate invoice amount based on treatment details.
     */
    private BigDecimal calculateInvoiceAmount(String traitement) {
        // Base consultation fee
        BigDecimal baseFee = new BigDecimal("50.00");

        // Additional fees based on treatment complexity
        BigDecimal treatmentFee = BigDecimal.ZERO;
        if (traitement != null && !traitement.trim().isEmpty()) {
            int treatmentLength = traitement.length();
            if (treatmentLength > 100) {
                treatmentFee = new BigDecimal("25.00");
            } else if (treatmentLength > 50) {
                treatmentFee = new BigDecimal("15.00");
            } else if (treatmentLength > 0) {
                treatmentFee = new BigDecimal("10.00");
            }
        }

        return baseFee.add(treatmentFee);
    }
}