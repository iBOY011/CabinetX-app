package com.gi.notificationservice.service.impl;

import com.gi.notificationservice.controller.NotificationWebSocketController;
import com.gi.notificationservice.exception.ResourceNotFoundException;
import com.gi.notificationservice.mapper.NotificationMapper;
import com.gi.notificationservice.model.entity.Notification;
import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import com.gi.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationWebSocketController webSocketController;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setRecipientId(1L);
        notification.setType(NotificationType.PATIENT_CONSULTATION);
        notification.setTitle("Test Title");
        notification.setContent("Test Content");
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreationDate(LocalDateTime.now());
        notification.setPatientId(1L);
        notification.setAppointmentId(1L);
        notification.setConsultationId(1L);

        notificationResponse = NotificationMapper.toResponse(notification);
    }

    @Test
    void sendPatientFollowingNotification_ShouldReturnNotificationResponse() {
        // Arrange
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        NotificationResponse result = notificationService.sendPatientFollowingNotification(1L, "John Doe");

        // Assert
        assertNotNull(result);
        assertEquals(NotificationType.PATIENT_FOLLOWING, result.getType());
        assertEquals("New Patient Following", result.getTitle());
        assertEquals("Patient John Doe is following you.", result.getContent());
        assertEquals(NotificationStatus.PENDING, result.getStatus());
        verify(repository, times(1)).save(any(Notification.class));
    }

    @Test
    void sendSubscriptionExpirationAlert_ShouldReturnNotificationResponse() {
        // Arrange
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        NotificationResponse result = notificationService.sendSubscriptionExpirationAlert(1L, "Clinic ABC", 5);

        // Assert
        assertNotNull(result);
        assertEquals(NotificationType.SUBSCRIPTION_EXPIRATION_ALERT, result.getType());
        assertEquals("Subscription Expiration Alert", result.getTitle());
        assertEquals("The subscription for clinic Clinic ABC will expire in 5 days.", result.getContent());
        verify(repository, times(1)).save(any(Notification.class));
    }

    @Test
    void sendPatientConsultationNotification_ShouldReturnNotificationResponseAndSendWebSocket() {
        // Arrange
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        NotificationResponse result = notificationService.sendPatientConsultationNotification(
                1L, 1L, 1L, "John Doe", 30, "Checkup", "10:00 AM");

        // Assert
        assertNotNull(result);
        assertEquals(NotificationType.PATIENT_CONSULTATION, result.getType());
        assertEquals("Nouveau patient en consultation", result.getTitle());
        assertTrue(result.getContent().contains("John Doe"));
        assertTrue(result.getContent().contains("30"));
        assertTrue(result.getContent().contains("Checkup"));
        assertTrue(result.getContent().contains("10:00 AM"));
        verify(repository, times(1)).save(any(Notification.class));
        verify(webSocketController, times(1)).sendNotificationToDoctor(eq(1L), any(NotificationResponse.class));
    }

    @Test
    void sendBillingReadyNotification_ShouldReturnNotificationResponseAndSendWebSocket() {
        // Arrange
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        NotificationResponse result = notificationService.sendBillingReadyNotification(
                1L, 1L, 1L, 1L, "John Doe", "Flu", "Rest");

        // Assert
        assertNotNull(result);
        assertEquals(NotificationType.BILLING_READY, result.getType());
        assertEquals("Consultation terminée - Facturation requise", result.getTitle());
        assertTrue(result.getContent().contains("John Doe"));
        assertTrue(result.getContent().contains("Flu"));
        verify(repository, times(1)).save(any(Notification.class));
        verify(webSocketController, times(1)).sendNotificationToDoctor(eq(1L), any(NotificationResponse.class));
    }

    @Test
    void markAsRead_ShouldUpdateStatusAndReturnResponse() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(notification));
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        NotificationResponse result = notificationService.markAsRead(1L);

        // Assert
        assertNotNull(result);
        assertEquals(NotificationStatus.READ, result.getStatus());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(notification);
    }

    @Test
    void markAsRead_ShouldThrowExceptionWhenNotificationNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(1L));
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Notification.class));
    }

    @Test
    void listNotifications_ShouldReturnListOfNotifications() {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification);
        when(repository.findByRecipientId(1L)).thenReturn(notifications);

        // Act
        List<NotificationResponse> result = notificationService.listNotifications(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByRecipientId(1L);
    }

    @Test
    void listUnreadNotifications_ShouldReturnListOfUnreadNotifications() {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification);
        when(repository.findByRecipientIdAndStatus(1L, NotificationStatus.PENDING)).thenReturn(notifications);

        // Act
        List<NotificationResponse> result = notificationService.listUnreadNotifications(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByRecipientIdAndStatus(1L, NotificationStatus.PENDING);
    }
}