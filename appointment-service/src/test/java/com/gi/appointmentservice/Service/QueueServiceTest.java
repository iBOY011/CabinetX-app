package com.gi.appointmentservice.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Repository.RDVRepository;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private RDVRepository rdvRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private QueueService queueService;

    private RendezVous rendezVous;
    private PatientInfoDTO patientInfo;
    private RDVResponse rdvResponse;

    @BeforeEach
    void setUp() {
        rendezVous = new RendezVous();
        rendezVous.setId(1L);
        rendezVous.setPatientId(1L);
        rendezVous.setCabinetId(1L);
        rendezVous.setDate(LocalDate.now().plusDays(1));
        rendezVous.setHeure_debut(java.time.LocalTime.of(10, 0));
        rendezVous.setHeure_fin(java.time.LocalTime.of(11, 0));
        rendezVous.setMotifRDV(com.gi.appointmentservice.Model.Enum.MotifRDV.CONSULTATION);
        rendezVous.setStatutRDV(StatutRDV.CONFIRME);
        rendezVous.setQueuePosition(null);

        patientInfo = new PatientInfoDTO();
        patientInfo.setPrenom("John");
        patientInfo.setNom("Doe");
        patientInfo.setCin("12345678");
        patientInfo.setDateNaissance(LocalDate.of(1990, 1, 1));

        rdvResponse = new RDVResponse();
        rdvResponse.setId(1L);
        rdvResponse.setPatientId(1L);
        rdvResponse.setCabinetId(1L);
        rdvResponse.setStatutRDV(StatutRDV.EN_ATTENTE);
        rdvResponse.setQueuePosition(1);
    }

    @Test
    void getQueueByDateAndCabinet_ValidRequest_ShouldReturnSortedQueue() {
        // Arrange
        List<RendezVous> queueAppointments = Arrays.asList(rendezVous);
        when(rdvRepository.findByCabinetIdAndDateAndStatutRDV(1L, LocalDate.now(), StatutRDV.EN_ATTENTE))
                .thenReturn(queueAppointments);
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        List<RDVResponse> result = queueService.getQueueByDateAndCabinet(LocalDate.now(), 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rdvRepository).findByCabinetIdAndDateAndStatutRDV(1L, LocalDate.now(), StatutRDV.EN_ATTENTE);
    }

    @Test
    void addToQueue_ValidAppointment_ShouldAddToQueue() {
        // Arrange
        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.of(rendezVous));
        when(rdvRepository.findMaxQueuePositionByCabinetAndDate(1L, rendezVous.getDate())).thenReturn(null);
        doAnswer(invocation -> invocation.getArgument(0)).when(rdvRepository).save(any(RendezVous.class));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = queueService.addToQueue(1L);

        // Assert
        assertNotNull(result);
        assertEquals(StatutRDV.EN_ATTENTE, rendezVous.getStatutRDV());
        assertEquals(1, rendezVous.getQueuePosition());
        verify(rdvRepository).save(rendezVous);
    }

    @Test
    void addToQueue_AppointmentAlreadyInQueue_ShouldThrowException() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.EN_ATTENTE);
        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.of(rendezVous));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queueService.addToQueue(1L));
        assertEquals("Appointment is already in queue", exception.getMessage());
    }

    @Test
    void addToQueue_AppointmentNotFound_ShouldThrowException() {
        // Arrange
        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queueService.addToQueue(1L));
        assertEquals("Appointment not found with id: 1", exception.getMessage());
    }

    @Test
    void removeFromQueue_ValidAppointment_ShouldRemoveFromQueue() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.EN_ATTENTE);
        rendezVous.setQueuePosition(2);
        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.of(rendezVous));
        doAnswer(invocation -> invocation.getArgument(0)).when(rdvRepository).save(any(RendezVous.class));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = queueService.removeFromQueue(1L);

        // Assert
        assertNotNull(result);
        assertEquals(StatutRDV.CONFIRME, rendezVous.getStatutRDV());
        assertNull(rendezVous.getQueuePosition());
        verify(rdvRepository).save(rendezVous);
    }

    @Test
    void removeFromQueue_AppointmentNotInQueue_ShouldThrowException() {
        // Arrange
        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.of(rendezVous));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queueService.removeFromQueue(1L));
        assertEquals("Appointment is not in queue", exception.getMessage());
    }

    @Test
    void callNext_ValidAppointment_ShouldMoveToConsultation() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.EN_ATTENTE);
        rendezVous.setQueuePosition(1);

        // Mock user client for doctor retrieval
        var userInfo = new com.gi.appointmentservice.Model.DTO.UserInfoDTO();
        userInfo.setId(1L);
        userInfo.setRole("MEDCIN");
        userInfo.setFirstName("Dr");
        userInfo.setLastName("Smith");

        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.of(rendezVous));
        doAnswer(invocation -> invocation.getArgument(0)).when(rdvRepository).save(any(RendezVous.class));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);
        when(userClient.getUsersByClinic(1L)).thenReturn(Arrays.asList(userInfo));

        // Act
        RDVResponse result = queueService.callNext(1L);

        // Assert
        assertNotNull(result);
        assertEquals(StatutRDV.EN_CONSULTATION, rendezVous.getStatutRDV());
        assertNull(rendezVous.getQueuePosition());
        verify(rdvRepository).save(rendezVous);
        verify(notificationClient).sendPatientConsultationNotification(
                eq(1L), eq(1L), eq(1L), anyString(), anyInt(), anyString(), anyString());
    }

    @Test
    void callNext_AppointmentNotInQueue_ShouldThrowException() {
        // Arrange
        when(rdvRepository.findById(1L)).thenReturn(java.util.Optional.of(rendezVous));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queueService.callNext(1L));
        assertEquals("Appointment is not in queue", exception.getMessage());
    }

    @Test
    void reorderQueue_ValidRequest_ShouldReorderAppointments() {
        // Arrange
        RendezVous appointment1 = new RendezVous();
        appointment1.setId(1L);
        appointment1.setStatutRDV(StatutRDV.EN_ATTENTE);

        RendezVous appointment2 = new RendezVous();
        appointment2.setId(2L);
        appointment2.setStatutRDV(StatutRDV.EN_ATTENTE);

        List<RendezVous> appointments = Arrays.asList(appointment1, appointment2);
        List<Long> newOrder = Arrays.asList(2L, 1L);

        when(rdvRepository.findAllById(newOrder)).thenReturn(appointments);
        when(rdvRepository.saveAll(anyList())).thenReturn(appointments);

        // Act
        List<RDVResponse> result = queueService.reorderQueue(1L, LocalDate.now(), newOrder);

        // Assert
        assertNotNull(result);
        verify(rdvRepository).saveAll(appointments);
    }

    @Test
    void reorderQueue_AppointmentNotFound_ShouldThrowException() {
        // Arrange
        List<Long> appointmentIds = Arrays.asList(1L, 2L);
        when(rdvRepository.findAllById(appointmentIds)).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queueService.reorderQueue(1L, LocalDate.now(), appointmentIds));
        assertEquals("Some appointments not found", exception.getMessage());
    }

    @Test
    void reorderQueue_AppointmentNotInQueue_ShouldThrowException() {
        // Arrange
        RendezVous appointment = new RendezVous();
        appointment.setId(1L);
        appointment.setStatutRDV(StatutRDV.CONFIRME); // Not in queue

        List<Long> appointmentIds = Arrays.asList(1L);
        when(rdvRepository.findAllById(appointmentIds)).thenReturn(Arrays.asList(appointment));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queueService.reorderQueue(1L, LocalDate.now(), appointmentIds));
        assertEquals("Appointment 1 is not in queue", exception.getMessage());
    }
}