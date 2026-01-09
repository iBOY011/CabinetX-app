package com.gi.notificationservice.mapper;

import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.entity.Notification;

public class NotificationMapper {
    public static NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setRecipientId(notification.getRecipientId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setStatus(notification.getStatus());
        response.setCreationDate(notification.getCreationDate());
        response.setPatientId(notification.getPatientId());
        response.setAppointmentId(notification.getAppointmentId());
        response.setConsultationId(notification.getConsultationId());
        return response;
    }
}