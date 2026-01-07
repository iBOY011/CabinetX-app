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
     * Send a notification to a specific doctor via WebSocket
     * Topic: /topic/doctor/{doctorId}/notifications
     */
    public void sendNotificationToDoctor(Long doctorId, NotificationResponse notification) {
        String destination = "/topic/doctor/" + doctorId + "/notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }

    /**
     * Broadcast notification to all users (if needed in the future)
     */
    public void broadcastNotification(NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
