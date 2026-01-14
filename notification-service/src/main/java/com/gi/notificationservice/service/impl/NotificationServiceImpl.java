package com.gi.notificationservice.service.impl;

import com.gi.notificationservice.client.UserClient;
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
    private final UserClient userClient;

    public NotificationServiceImpl(NotificationRepository repository,
            NotificationWebSocketController webSocketController,
            UserClient userClient) {
        this.repository = repository;
        this.webSocketController = webSocketController;
        this.userClient = userClient;
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
    public NotificationResponse sendNextPatientRequest(Long doctorId, String doctorName, Long clinicId) {
        System.out.println("===== NOTIFICATION SERVICE: NEXT PATIENT REQUEST =====");
        System.out.println("→ Doctor: " + doctorName + " (ID: " + doctorId + ")");
        System.out.println("→ Clinic ID: " + clinicId);
        
        // Trouver les secrétaires du cabinet
        List<UserClient.UserDTO> secretaries = userClient.getUsersByCabinetAndRole(clinicId, "SECRETAIRE");
        
        if (secretaries == null || secretaries.isEmpty()) {
            System.out.println("⚠ No secretary found for clinic ID: " + clinicId);
            throw new ResourceNotFoundException("Aucune secrétaire trouvée pour ce cabinet");
        }
        
        System.out.println("✓ Found " + secretaries.size() + " secretary(ies)");
        
        NotificationResponse response = null;
        
        // Envoyer une notification à chaque secrétaire
        for (UserClient.UserDTO secretary : secretaries) {
            System.out.println("→ Sending to secretary: " + secretary.getFirstName() + " " + secretary.getLastName() + " (ID: " + secretary.getId() + ")");
            
            Notification notification = new Notification();
            notification.setRecipientId(secretary.getId());
            notification.setType(NotificationType.PATIENT_FOLLOWING);
            notification.setTitle("Demande de patient suivant");
            notification.setContent(String.format("%s demande le patient suivant", doctorName));
            notification.setStatus(NotificationStatus.PENDING);
            notification.setCreationDate(LocalDateTime.now());
            Notification saved = repository.save(notification);
            System.out.println("✓ Notification saved to database with ID: " + saved.getId());

            // Convert to response DTO
            response = NotificationMapper.toResponse(saved);

            // Send via WebSocket
            webSocketController.sendNotificationToDoctor(secretary.getId(), response);
        }
        
        System.out.println("===== NEXT PATIENT REQUEST COMPLETED =====");
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