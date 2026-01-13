package com.gi.notificationservice.controller;

import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import com.gi.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationResponse = new NotificationResponse();
        notificationResponse.setId(1L);
        notificationResponse.setRecipientId(1L);
        notificationResponse.setType(NotificationType.PATIENT_CONSULTATION);
        notificationResponse.setTitle("Test Title");
        notificationResponse.setContent("Test Content");
        notificationResponse.setStatus(NotificationStatus.PENDING);
        notificationResponse.setCreationDate(LocalDateTime.now());
        notificationResponse.setPatientId(1L);
        notificationResponse.setAppointmentId(1L);
        notificationResponse.setConsultationId(1L);
    }

    @Test
    void listNotifications_ShouldReturnListOfNotifications() {
        // Arrange
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.listNotifications(1L)).thenReturn(notifications);

        // Act
        ResponseEntity<List<NotificationResponse>> result = notificationController.listNotifications(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(notifications, result.getBody());
        verify(notificationService, times(1)).listNotifications(1L);
    }

    @Test
    void listUnreadNotifications_ShouldReturnListOfUnreadNotifications() {
        // Arrange
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.listUnreadNotifications(1L)).thenReturn(notifications);

        // Act
        ResponseEntity<List<NotificationResponse>> result = notificationController.listUnreadNotifications(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(notifications, result.getBody());
        verify(notificationService, times(1)).listUnreadNotifications(1L);
    }

    @Test
    void markAsRead_ShouldReturnUpdatedNotification() {
        // Arrange
        when(notificationService.markAsRead(1L)).thenReturn(notificationResponse);

        // Act
        ResponseEntity<NotificationResponse> result = notificationController.markAsRead(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(notificationResponse, result.getBody());
        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    void sendPatientConsultation_ShouldReturnNotificationResponse() {
        // Arrange
        when(notificationService.sendPatientConsultationNotification(1L, 1L, 1L, "John Doe", 30, "Checkup", "10:00 AM"))
                .thenReturn(notificationResponse);

        // Act
        ResponseEntity<NotificationResponse> result = notificationController.sendPatientConsultation(
                1L, 1L, 1L, "John Doe", 30, "Checkup", "10:00 AM");

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(notificationResponse, result.getBody());
        verify(notificationService, times(1)).sendPatientConsultationNotification(
                1L, 1L, 1L, "John Doe", 30, "Checkup", "10:00 AM");
    }

    @Test
    void sendBillingReady_ShouldReturnNotificationResponse() {
        // Arrange
        when(notificationService.sendBillingReadyNotification(1L, 1L, 1L, 1L, "John Doe", "Flu", "Rest"))
                .thenReturn(notificationResponse);

        // Act
        ResponseEntity<NotificationResponse> result = notificationController.sendBillingReady(
                1L, 1L, 1L, 1L, "John Doe", "Flu", "Rest");

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(notificationResponse, result.getBody());
        verify(notificationService, times(1)).sendBillingReadyNotification(
                1L, 1L, 1L, 1L, "John Doe", "Flu", "Rest");
    }
}