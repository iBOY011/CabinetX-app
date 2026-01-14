package com.gi.notificationservice.service.impl;

import com.gi.notificationservice.exception.ResourceNotFoundException;
import com.gi.notificationservice.mapper.NotificationMapper;
import com.gi.notificationservice.model.entity.Notification;
import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import com.gi.notificationservice.repository.NotificationRepository;
import com.gi.notificationservice.service.NotificationService;
import com.gi.notificationservice.controller.NotificationWebSocketController;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationWebSocketController webSocketController;

    public NotificationServiceImpl(NotificationRepository repository,
            NotificationWebSocketController webSocketController) {
        this.repository = repository;
        this.webSocketController = webSocketController;
    }

    @Override
    public NotificationResponse sendPatientFollowingNotification(Long doctorId, String patientName) {
        Notification notification = new Notification();
        notification.setRecipientId(doctorId);
        notification.setType(NotificationType.PATIENT_FOLLOWING);
        notification.setTitle("New Patient Following");
        notification.setContent("Patient " + patientName + " is following you.");
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreationDate(LocalDateTime.now());
        Notification saved = repository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    public NotificationResponse sendSubscriptionExpirationAlert(Long adminId, String clinicName, int daysRemaining) {
        Notification notification = new Notification();
        notification.setRecipientId(adminId);
        notification.setType(NotificationType.SUBSCRIPTION_EXPIRATION_ALERT);
        notification.setTitle("Subscription Expiration Alert");
        notification.setContent(
                "The subscription for clinic " + clinicName + " will expire in " + daysRemaining + " days.");
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreationDate(LocalDateTime.now());
        Notification saved = repository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    public NotificationResponse sendPatientConsultationNotification(Long doctorId, Long appointmentId, Long patientId,
            String patientName, Integer patientAge, String reason, String appointmentTime) {
        Notification notification = new Notification();
        notification.setRecipientId(doctorId);
        notification.setType(NotificationType.PATIENT_CONSULTATION);
        notification.setTitle("Nouveau patient en consultation");
        notification.setContent(String.format("%s (%d ans) vous attend - Motif: %s - Prévu à %s",
                patientName, patientAge, reason, appointmentTime));
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreationDate(LocalDateTime.now());
        notification.setAppointmentId(appointmentId);
        notification.setPatientId(patientId);
        Notification saved = repository.save(notification);

        // Convert to response DTO
        NotificationResponse response = NotificationMapper.toResponse(saved);

        // Send via WebSocket for real-time delivery
        webSocketController.sendNotificationToDoctor(doctorId, response);

        return response;
    }

    @Override
    public NotificationResponse sendBillingReadyNotification(Long secretaryId, Long consultationId, Long appointmentId,
            Long patientId, String patientName, String diagnostic, String traitement) {
        System.out.println("===== NOTIFICATION SERVICE: BILLING READY NOTIFICATION =====");
        System.out.println("→ Secretary ID: " + secretaryId);
        System.out.println("→ Consultation ID: " + consultationId);
        System.out.println("→ Appointment ID: " + appointmentId);
        System.out.println("→ Patient: " + patientName + " (ID: " + patientId + ")");
        System.out.println("→ Diagnostic: " + diagnostic);
        
        Notification notification = new Notification();
        notification.setRecipientId(secretaryId);
        notification.setType(NotificationType.BILLING_READY);
        notification.setTitle("Consultation terminée - Facturation requise");
        notification.setContent(String.format("La consultation de %s est terminée. Diagnostic: %s. Prête pour facturation.",
                patientName, diagnostic));
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreationDate(LocalDateTime.now());
        notification.setAppointmentId(appointmentId);
        notification.setPatientId(patientId);
        notification.setConsultationId(consultationId);
        Notification saved = repository.save(notification);
        System.out.println("✓ Notification saved to database with ID: " + saved.getId());

        // Convert to response DTO
        NotificationResponse response = NotificationMapper.toResponse(saved);

        // Send via WebSocket for real-time delivery to secretary
        System.out.println("→ Sending WebSocket notification to secretary ID: " + secretaryId);
        webSocketController.sendNotificationToDoctor(secretaryId, response);
        System.out.println("===== BILLING READY NOTIFICATION COMPLETED =====");

        return response;
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadDate(LocalDateTime.now());
        Notification saved = repository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    public List<NotificationResponse> listNotifications(Long recipientId) {
        return repository.findByRecipientId(recipientId).stream()
                .map(NotificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> listUnreadNotifications(Long recipientId) {
        return repository.findByRecipientIdAndStatus(recipientId, NotificationStatus.PENDING).stream()
                .map(NotificationMapper::toResponse)
                .collect(Collectors.toList());
    }
}