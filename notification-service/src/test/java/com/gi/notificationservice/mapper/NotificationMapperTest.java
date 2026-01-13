package com.gi.notificationservice.mapper;

import com.gi.notificationservice.model.entity.Notification;
import com.gi.notificationservice.model.dto.response.NotificationResponse;
import com.gi.notificationservice.model.enums.NotificationStatus;
import com.gi.notificationservice.model.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    @Test
    void toResponse_ShouldMapAllFieldsCorrectly() {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRecipientId(2L);
        notification.setType(NotificationType.PATIENT_CONSULTATION);
        notification.setTitle("Test Title");
        notification.setContent("Test Content");
        notification.setStatus(NotificationStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        notification.setCreationDate(now);
        notification.setPatientId(3L);
        notification.setAppointmentId(4L);
        notification.setConsultationId(5L);

        // Act
        NotificationResponse response = NotificationMapper.toResponse(notification);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getRecipientId());
        assertEquals(NotificationType.PATIENT_CONSULTATION, response.getType());
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals(NotificationStatus.PENDING, response.getStatus());
        assertEquals(now, response.getCreationDate());
        assertEquals(3L, response.getPatientId());
        assertEquals(4L, response.getAppointmentId());
        assertEquals(5L, response.getConsultationId());
    }

    @Test
    void toResponse_ShouldHandleNullOptionalFields() {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRecipientId(2L);
        notification.setType(NotificationType.PATIENT_FOLLOWING);
        notification.setTitle("Test Title");
        notification.setContent("Test Content");
        notification.setStatus(NotificationStatus.READ);
        notification.setCreationDate(LocalDateTime.now());
        // patientId, appointmentId, consultationId remain null

        // Act
        NotificationResponse response = NotificationMapper.toResponse(notification);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertNull(response.getPatientId());
        assertNull(response.getAppointmentId());
        assertNull(response.getConsultationId());
    }
}