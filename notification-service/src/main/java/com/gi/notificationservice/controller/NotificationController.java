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
        return ResponseEntity.ok(service.sendBillingReadyNotification(
                secretaryId, consultationId, appointmentId, patientId, patientName, diagnostic, traitement));
    }
}