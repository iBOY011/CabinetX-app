package com.gi.consultationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    
    @PostMapping("/api/notifications/billing-ready")
    void sendBillingReadyNotification(
            @RequestParam("secretaryId") Long secretaryId,
            @RequestParam("consultationId") Long consultationId,
            @RequestParam("appointmentId") Long appointmentId,
            @RequestParam("patientId") Long patientId,
            @RequestParam("patientName") String patientName,
            @RequestParam("diagnostic") String diagnostic,
            @RequestParam("traitement") String traitement
    );
}
