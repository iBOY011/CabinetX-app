package com.gi.appointmentservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client to communicate with notification-service
 */
@Service
public class NotificationClient {

    private final WebClient webClient;

    public NotificationClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Send patient consultation notification to a doctor
     * Calls: POST
     * http://NOTIFICATION-SERVICE/api/notifications/patient-consultation
     */
    public void sendPatientConsultationNotification(
            Long doctorId,
            Long appointmentId,
            String patientName,
            Integer patientAge,
            String reason,
            String appointmentTime) {

        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("NOTIFICATION-SERVICE")
                            .path("/api/notifications/patient-consultation")
                            .queryParam("doctorId", doctorId)
                            .queryParam("appointmentId", appointmentId)
                            .queryParam("patientName", patientName)
                            .queryParam("patientAge", patientAge)
                            .queryParam("reason", reason)
                            .queryParam("appointmentTime", appointmentTime)
                            .build())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe(); // Non-blocking call

            System.out.println("Notification sent to doctor ID: " + doctorId + " for patient: " + patientName);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            // Don't throw - notification failure shouldn't block the main operation
        }
    }
}
