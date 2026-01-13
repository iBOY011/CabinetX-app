package com.gi.appointmentservice.Controller;

import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.DTO.UpdateDto;
import com.gi.appointmentservice.Model.Enum.MotifRDV;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Service.RDVService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RDVControllerTest {

    @Mock
    private RDVService rdvService;

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private RDVController rdvController;

    private RDVRequest rdvRequest;
    private RDVResponse rdvResponse;
    private UpdateDto updateDto;

    @BeforeEach
    void setUp() {
        rdvRequest = new RDVRequest();
        rdvRequest.setPatientId(1L);
        rdvRequest.setCabinetId(1L);
        rdvRequest.setDate(LocalDate.now().plusDays(1));
        rdvRequest.setHeure_debut(LocalTime.of(10, 0));
        rdvRequest.setHeure_fin(LocalTime.of(11, 0));
        rdvRequest.setMotifRDV(MotifRDV.CONSULTATION);
        rdvRequest.setNotes("Test appointment");

        rdvResponse = new RDVResponse();
        rdvResponse.setId(1L);
        rdvResponse.setPatientId(1L);
        rdvResponse.setCabinetId(1L);
        rdvResponse.setDate(LocalDate.now().plusDays(1));
        rdvResponse.setHeure_debut(LocalTime.of(10, 0));
        rdvResponse.setHeure_fin(LocalTime.of(11, 0));
        rdvResponse.setMotifRDV(MotifRDV.CONSULTATION);
        rdvResponse.setNotes("Test appointment");
        rdvResponse.setPrenom("John");
        rdvResponse.setNom("Doe");
        rdvResponse.setCin("12345678");
        rdvResponse.setDateNaissance(LocalDate.of(1990, 1, 1));
        rdvResponse.setStatutRDV(StatutRDV.CONFIRME);
        rdvResponse.setQueuePosition(1);

        updateDto = new UpdateDto();
        updateDto.setDate(LocalDate.now().plusDays(2));
        updateDto.setHeure_debut(LocalTime.of(14, 0));
        updateDto.setHeure_fin(LocalTime.of(15, 0));
        updateDto.setMotifRDV(MotifRDV.CONSULTATION);
        updateDto.setNotes("Updated appointment");
    }

    @Test
    void welcome_ShouldReturnWelcomeMessageAndSendToKafka() {
        // Act
        String result = rdvController.welcome();

        // Assert
        assertEquals("Welcome to the Appointment Service!", result);
        verify(streamBridge, times(1)).send(eq("queue-topic"), eq("Welcome endpoint called!"));
    }

    @Test
    void createRendezVous_ShouldReturnCreatedResponse() {
        // Arrange
        when(rdvService.createRendezVous(any(RDVRequest.class))).thenReturn(rdvResponse);

        // Act
        ResponseEntity<RDVResponse> response = rdvController.createRendezVous(rdvRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(rdvResponse.getId(), response.getBody().getId());
        verify(rdvService, times(1)).createRendezVous(rdvRequest);
    }

    @Test
    void getRendezVousById_ShouldReturnAppointment() {
        // Arrange
        when(rdvService.getRendezVousById(1L)).thenReturn(rdvResponse);

        // Act
        ResponseEntity<RDVResponse> response = rdvController.getRendezVousById(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(rdvService, times(1)).getRendezVousById(1L);
    }

    @Test
    void getTodayRendezVousForPatient_ShouldReturnTodayAppointment() {
        // Arrange
        when(rdvService.getTodayRendezVousForPatient(1L)).thenReturn(rdvResponse);

        // Act
        ResponseEntity<RDVResponse> response = rdvController.getTodayRendezVousForPatient(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getPatientId());
        verify(rdvService, times(1)).getTodayRendezVousForPatient(1L);
    }

    @Test
    void updateRendezVous_ShouldReturnUpdatedResponse() {
        // Arrange
        RDVResponse updatedResponse = rdvResponse;
        updatedResponse.setDate(updateDto.getDate());
        updatedResponse.setHeure_debut(updateDto.getHeure_debut());
        updatedResponse.setHeure_fin(updateDto.getHeure_fin());
        updatedResponse.setNotes(updateDto.getNotes());

        when(rdvService.updateRendezVous(1L, updateDto)).thenReturn(updatedResponse);

        // Act
        ResponseEntity<RDVResponse> response = rdvController.updateRendezVous(1L, updateDto);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(updateDto.getDate(), response.getBody().getDate());
        verify(rdvService, times(1)).updateRendezVous(1L, updateDto);
    }

    @Test
    void deleteRendezVous_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = rdvController.deleteRendezVous(1L);

        // Assert
        assertEquals(204, response.getStatusCode().value());
        verify(rdvService, times(1)).deleteRendezVous(1L);
    }

    @Test
    void updateStatusRendezVous_ShouldReturnUpdatedResponse() {
        // Arrange
        RDVResponse statusUpdatedResponse = rdvResponse;
        statusUpdatedResponse.setStatutRDV(StatutRDV.ANNULE);

        when(rdvService.updateStatusRendezVous(1L, StatutRDV.ANNULE)).thenReturn(statusUpdatedResponse);

        // Act
        ResponseEntity<RDVResponse> response = rdvController.updateStatusRendezVous(1L, StatutRDV.ANNULE);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(StatutRDV.ANNULE, response.getBody().getStatutRDV());
        verify(rdvService, times(1)).updateStatusRendezVous(1L, StatutRDV.ANNULE);
    }

    @Test
    void getAppointmentsByDate_ShouldReturnAppointmentList() {
        // Arrange
        LocalDate date = LocalDate.now();
        Long cabinetId = 1L;
        List<RDVResponse> appointments = Arrays.asList(rdvResponse);

        when(rdvService.getAppointmentsByDate(date, cabinetId)).thenReturn(appointments);

        // Act
        ResponseEntity<List<RDVResponse>> response = rdvController.getAppointmentsByDate(date, cabinetId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(rdvResponse.getId(), response.getBody().get(0).getId());
        verify(rdvService, times(1)).getAppointmentsByDate(date, cabinetId);
    }
}