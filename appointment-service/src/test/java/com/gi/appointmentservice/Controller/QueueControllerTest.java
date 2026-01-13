package com.gi.appointmentservice.Controller;

import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.Enum.MotifRDV;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueControllerTest {

    @Mock
    private QueueService queueService;

    @InjectMocks
    private QueueController queueController;

    private RDVResponse rdvResponse;
    private QueueController.ReorderQueueRequest reorderRequest;

    @BeforeEach
    void setUp() {
        rdvResponse = new RDVResponse();
        rdvResponse.setId(1L);
        rdvResponse.setPatientId(1L);
        rdvResponse.setCabinetId(1L);
        rdvResponse.setDate(LocalDate.now());
        rdvResponse.setHeure_debut(LocalTime.of(10, 0));
        rdvResponse.setHeure_fin(LocalTime.of(11, 0));
        rdvResponse.setMotifRDV(MotifRDV.CONSULTATION);
        rdvResponse.setNotes("Test appointment");
        rdvResponse.setPrenom("John");
        rdvResponse.setNom("Doe");
        rdvResponse.setCin("12345678");
        rdvResponse.setDateNaissance(LocalDate.of(1990, 1, 1));
        rdvResponse.setStatutRDV(StatutRDV.EN_ATTENTE);
        rdvResponse.setQueuePosition(1);

        reorderRequest = new QueueController.ReorderQueueRequest();
        reorderRequest.setCabinetId(1L);
        reorderRequest.setDate(LocalDate.now());
        reorderRequest.setAppointmentIds(Arrays.asList(3L, 1L, 2L));
    }

    @Test
    void getQueueByDate_ShouldReturnQueueList() {
        // Arrange
        LocalDate date = LocalDate.now();
        Long cabinetId = 1L;
        List<RDVResponse> queue = Arrays.asList(rdvResponse);

        when(queueService.getQueueByDateAndCabinet(date, cabinetId)).thenReturn(queue);

        // Act
        ResponseEntity<List<RDVResponse>> response = queueController.getQueueByDate(date, cabinetId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(rdvResponse.getId(), response.getBody().get(0).getId());
        verify(queueService, times(1)).getQueueByDateAndCabinet(date, cabinetId);
    }

    @Test
    void addToQueue_ShouldReturnUpdatedResponse() {
        // Arrange
        RDVResponse addedToQueueResponse = rdvResponse;
        addedToQueueResponse.setStatutRDV(StatutRDV.EN_ATTENTE);
        addedToQueueResponse.setQueuePosition(2);

        when(queueService.addToQueue(1L)).thenReturn(addedToQueueResponse);

        // Act
        ResponseEntity<RDVResponse> response = queueController.addToQueue(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(StatutRDV.EN_ATTENTE, response.getBody().getStatutRDV());
        assertEquals(2, response.getBody().getQueuePosition());
        verify(queueService, times(1)).addToQueue(1L);
    }

    @Test
    void removeFromQueue_ShouldReturnUpdatedResponse() {
        // Arrange
        RDVResponse removedFromQueueResponse = rdvResponse;
        removedFromQueueResponse.setStatutRDV(StatutRDV.CONFIRME);
        removedFromQueueResponse.setQueuePosition(null);

        when(queueService.removeFromQueue(1L)).thenReturn(removedFromQueueResponse);

        // Act
        ResponseEntity<RDVResponse> response = queueController.removeFromQueue(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(StatutRDV.CONFIRME, response.getBody().getStatutRDV());
        assertNull(response.getBody().getQueuePosition());
        verify(queueService, times(1)).removeFromQueue(1L);
    }

    @Test
    void callNext_ShouldReturnUpdatedResponse() {
        // Arrange
        RDVResponse calledNextResponse = rdvResponse;
        calledNextResponse.setStatutRDV(StatutRDV.EN_CONSULTATION);
        calledNextResponse.setQueuePosition(null);

        when(queueService.callNext(1L)).thenReturn(calledNextResponse);

        // Act
        ResponseEntity<RDVResponse> response = queueController.callNext(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(StatutRDV.EN_CONSULTATION, response.getBody().getStatutRDV());
        assertNull(response.getBody().getQueuePosition());
        verify(queueService, times(1)).callNext(1L);
    }

    @Test
    void reorderQueue_ShouldReturnReorderedQueue() {
        // Arrange
        List<RDVResponse> reorderedQueue = Arrays.asList(
            createRDVResponse(3L, 1),
            createRDVResponse(1L, 2),
            createRDVResponse(2L, 3)
        );

        when(queueService.reorderQueue(
            reorderRequest.getCabinetId(),
            reorderRequest.getDate(),
            reorderRequest.getAppointmentIds()
        )).thenReturn(reorderedQueue);

        // Act
        ResponseEntity<List<RDVResponse>> response = queueController.reorderQueue(reorderRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(3L, response.getBody().get(0).getId());
        assertEquals(1, response.getBody().get(0).getQueuePosition());
        verify(queueService, times(1)).reorderQueue(
            reorderRequest.getCabinetId(),
            reorderRequest.getDate(),
            reorderRequest.getAppointmentIds()
        );
    }

    private RDVResponse createRDVResponse(Long id, Integer queuePosition) {
        RDVResponse response = new RDVResponse();
        response.setId(id);
        response.setPatientId(1L);
        response.setCabinetId(1L);
        response.setDate(LocalDate.now());
        response.setHeure_debut(LocalTime.of(10, 0));
        response.setHeure_fin(LocalTime.of(11, 0));
        response.setMotifRDV(MotifRDV.CONSULTATION);
        response.setNotes("Test appointment");
        response.setPrenom("John");
        response.setNom("Doe");
        response.setCin("12345678");
        response.setDateNaissance(LocalDate.of(1990, 1, 1));
        response.setStatutRDV(StatutRDV.EN_ATTENTE);
        response.setQueuePosition(queuePosition);
        return response;
    }
}
