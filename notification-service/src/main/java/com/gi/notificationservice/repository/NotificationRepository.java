package com.gi.notificationservice.repository;

import com.gi.notificationservice.model.entity.Notification;
import com.gi.notificationservice.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdAndStatus(Long recipientId, NotificationStatus status);
    List<Notification> findByRecipientId(Long recipientId);
}