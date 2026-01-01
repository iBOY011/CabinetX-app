package com.gi.consultationservice.messaging;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrdonnanceCountClient {

    private static final String REQUEST_TOPIC = "ordonnance-count-request";
    private final Map<String, Long> cache = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrdonnanceCountClient(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OptionalLong getCountOrRequest(Long medecinId, Long cabinetId, OffsetDateTime start, OffsetDateTime end) {
        String key = correlationKey(medecinId, cabinetId, start, end);
        Long cached = cache.get(key);
        if (cached != null) {
            return OptionalLong.of(cached);
        }
        OrdonnanceCountRequest request = new OrdonnanceCountRequest(key, medecinId, cabinetId, start, end);
        kafkaTemplate.send(REQUEST_TOPIC, key, request);
        return OptionalLong.empty();
    }

    @KafkaListener(topics = "ordonnance-count-response", groupId = "consultation-service-ordonnance")
    public void handleResponse(OrdonnanceCountResponse response) {
        if (response == null || response.getCorrelationId() == null) {
            return;
        }
        cache.put(response.getCorrelationId(), response.getCount());
    }

    private String correlationKey(Long medecinId, Long cabinetId, OffsetDateTime start, OffsetDateTime end) {
        return medecinId + ":" + (cabinetId != null ? cabinetId : "_") + ":" + start.toString() + ":" + end.toString();
    }
}
