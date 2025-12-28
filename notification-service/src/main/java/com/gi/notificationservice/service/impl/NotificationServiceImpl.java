package com.gi.notificationservice.service.impl;

import com.gi.notificationservice.exception.ResourceNotFoundException;
import com.gi.notificationservice.mapper.NotificationMapper;
import com.gi.notificationservice.model.entity.Notification;
import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import com.gi.notificationservice.repository.NotificationRepository;
import com.gi.notificationservice.service.NotificationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
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
        notification.setContent("The subscription for clinic " + clinicName + " will expire in " + daysRemaining + " days.");
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreationDate(LocalDateTime.now());
        Notification saved = repository.save(notification);
        return NotificationMapper.toResponse(saved);
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