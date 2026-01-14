package com.gi.notificationservice.controller;

import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/{recipientId}")
    public ResponseEntity<List<NotificationResponse>> listNotifications(@PathVariable Long recipientId) {
        return ResponseEntity.ok(service.listNotifications(recipientId));
    }

    @GetMapping("/{recipientId}/unread")
    public ResponseEntity<List<NotificationResponse>> listUnreadNotifications(@PathVariable Long recipientId) {
        return ResponseEntity.ok(service.listUnreadNotifications(recipientId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(service.markAsRead(notificationId));
    }

    @PostMapping("/patient-consultation")
    public ResponseEntity<NotificationResponse> sendPatientConsultation(
            @RequestParam Long doctorId,
            @RequestParam Long appointmentId,
            @RequestParam Long patientId,
            @RequestParam String patientName,
            @RequestParam Integer patientAge,
            @RequestParam String reason,
            @RequestParam String appointmentTime) {
        return ResponseEntity.ok(service.sendPatientConsultationNotification(
                doctorId, appointmentId, patientId, patientName, patientAge, reason, appointmentTime));
    }

    @PostMapping("/billing-ready")
    public ResponseEntity<NotificationResponse> sendBillingReady(
            @RequestParam Long secretaryId,
            @RequestParam Long consultationId,
            @RequestParam Long appointmentId,
            @RequestParam Long patientId,
            @RequestParam String patientName,
            @RequestParam String diagnostic,
            @RequestParam String traitement) {
        System.out.println("===== NOTIFICATION CONTROLLER: /billing-ready endpoint called =====");
        System.out.println("→ Received request for secretary ID: " + secretaryId);
        System.out.println("→ Consultation: " + consultationId + ", Appointment: " + appointmentId);
        System.out.println("→ Patient: " + patientName + " (ID: " + patientId + ")");
        return ResponseEntity.ok(service.sendBillingReadyNotification(
                secretaryId, consultationId, appointmentId, patientId, patientName, diagnostic, traitement));
    }

    @PostMapping("/request-next-patient")
    public ResponseEntity<NotificationResponse> requestNextPatient(
            @RequestParam Long doctorId,
            @RequestParam String doctorName,
            @RequestParam Long clinicId) {
        System.out.println("===== NOTIFICATION CONTROLLER: /request-next-patient endpoint called =====");
        System.out.println("→ Doctor: " + doctorName + " (ID: " + doctorId + ")");
        System.out.println("→ Clinic ID: " + clinicId);
        return ResponseEntity.ok(service.sendNextPatientRequest(doctorId, doctorName, clinicId));
    }
}