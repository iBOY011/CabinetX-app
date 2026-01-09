package com.gi.notificationservice.service;

import com.gi.notificationservice.model.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationService {
    NotificationResponse sendPatientFollowingNotification(Long doctorId, String patientName);

    NotificationResponse sendSubscriptionExpirationAlert(Long adminId, String clinicName, int daysRemaining);

    NotificationResponse sendPatientConsultationNotification(Long doctorId, Long appointmentId, Long patientId,
            String patientName, Integer patientAge, String reason, String appointmentTime);

    NotificationResponse sendBillingReadyNotification(Long secretaryId, Long consultationId, Long appointmentId, 
            Long patientId, String patientName, String diagnostic, String traitement);

    NotificationResponse markAsRead(Long notificationId);

    List<NotificationResponse> listNotifications(Long recipientId);

    List<NotificationResponse> listUnreadNotifications(Long recipientId);
}