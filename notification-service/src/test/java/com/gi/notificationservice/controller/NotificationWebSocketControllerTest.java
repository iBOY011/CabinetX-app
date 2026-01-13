package com.gi.notificationservice.controller;

import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationWebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationWebSocketController webSocketController;

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
    }

    @Test
    void sendNotificationToUser_ShouldSendToCorrectTopic() {
        // Act
        webSocketController.sendNotificationToUser(1L, notificationResponse);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend(
                "/topic/doctor/1/notifications", notificationResponse);
    }

    @Test
    void sendNotificationToDoctor_ShouldCallSendNotificationToUser() {
        // Act
        webSocketController.sendNotificationToDoctor(1L, notificationResponse);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend(
                "/topic/doctor/1/notifications", notificationResponse);
    }

    @Test
    void broadcastNotification_ShouldSendToBroadcastTopic() {
        // Act
        webSocketController.broadcastNotification(notificationResponse);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend(
                "/topic/notifications", notificationResponse);
    }
}