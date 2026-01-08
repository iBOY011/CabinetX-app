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
            Long patientId,
            String patientName,
            Integer patientAge,
            String reason,
            String appointmentTime) {

        System.out.println("[NotificationClient] Starting notification send - doctorId: " + doctorId + ", patient: " + patientName);
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("NOTIFICATION-SERVICE")
                            .path("/api/notifications/patient-consultation")
                            .queryParam("doctorId", doctorId)
                            .queryParam("appointmentId", appointmentId)
                            .queryParam("patientId", patientId)
                            .queryParam("patientName", patientName)
                            .queryParam("patientAge", patientAge)
                            .queryParam("reason", reason)
                            .queryParam("appointmentTime", appointmentTime)
                            .build())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(result -> System.out.println("[NotificationClient] HTTP call completed successfully for doctorId: " + doctorId))
                    .doOnError(error -> System.err.println("[NotificationClient] HTTP call failed: " + error.getMessage()))
                    .subscribe(); // Non-blocking call

            System.out.println("[NotificationClient] Notification HTTP request initiated for doctor ID: " + doctorId + " for patient: " + patientName);
        } catch (Exception e) {
            System.err.println("[NotificationClient] Exception during notification send: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - notification failure shouldn't block the main operation
        }
    }
}
