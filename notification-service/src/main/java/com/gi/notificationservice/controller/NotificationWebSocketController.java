package com.gi.notificationservice.controller;

import com.gi.notificationservice.model.dto.response.NotificationResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for pushing notifications to clients in real-time
 */
@Controller
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a notification to a specific user via WebSocket
     * Topic: /topic/doctor/{userId}/notifications
     * Note: The topic name uses "doctor" for legacy reasons, but it works for all user types
     * (doctors, secretaries, etc.) since the frontend subscribes using the user ID
     */
    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        String destination = "/topic/doctor/" + userId + "/notifications";
        System.out.println("[NotificationWebSocketController] Sending notification to user " + userId + " at topic: " + destination);
        messagingTemplate.convertAndSend(destination, notification);
        System.out.println("[NotificationWebSocketController] ✓ Notification sent to WebSocket topic");
    }

    /**
     * Send a notification to a specific doctor via WebSocket
     * Topic: /topic/doctor/{doctorId}/notifications
     * @deprecated Use sendNotificationToUser instead
     */
    @Deprecated
    public void sendNotificationToDoctor(Long doctorId, NotificationResponse notification) {
        sendNotificationToUser(doctorId, notification);
    }

    /**
     * Broadcast notification to all users (if needed in the future)
     */
    public void broadcastNotification(NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
