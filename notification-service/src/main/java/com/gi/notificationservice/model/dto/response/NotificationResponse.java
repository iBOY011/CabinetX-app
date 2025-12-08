package com.gi.notificationservice.model.dto.response;

import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private Long recipientId;
    private NotificationType type;
    private String title;
    private String content;
    private NotificationStatus status;
    private LocalDateTime creationDate;
}