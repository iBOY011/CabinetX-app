package com.gi.appointmentservice.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;
import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.DTO.UpdateDto;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.MotifRDV;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Repository.RDVRepository;

@ExtendWith(MockitoExtension.class)
class RDVServiceTest {

    @Mock
    private RDVRepository rdvRepository;

    @Mock
    private PatientClient patientClient;

    @InjectMocks
    private RDVService rdvService;

    private RDVRequest rdvRequest;
    private RendezVous rendezVous;
    private PatientInfoDTO patientInfo;
    private RDVResponse rdvResponse;

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

        rendezVous = new RendezVous();
        rendezVous.setId(1L);
        rendezVous.setPatientId(1L);
        rendezVous.setCabinetId(1L);
        rendezVous.setDate(LocalDate.now().plusDays(1));
        rendezVous.setHeure_debut(LocalTime.of(10, 0));
        rendezVous.setHeure_fin(LocalTime.of(11, 0));
        rendezVous.setMotifRDV(MotifRDV.CONSULTATION);
        rendezVous.setNotes("Test appointment");
        rendezVous.setStatutRDV(StatutRDV.CONFIRME);

        patientInfo = new PatientInfoDTO();
        patientInfo.setPrenom("John");
        patientInfo.setNom("Doe");
        patientInfo.setCin("12345678");
        patientInfo.setDateNaissance(LocalDate.of(1990, 1, 1));

        rdvResponse = new RDVResponse();
        rdvResponse.setId(1L);
        rdvResponse.setPatientId(1L);
        rdvResponse.setCabinetId(1L);
        rdvResponse.setDate(LocalDate.now().plusDays(1));
        rdvResponse.setHeure_debut(LocalTime.of(10, 0));
        rdvResponse.setHeure_fin(LocalTime.of(11, 0));
        rdvResponse.setMotifRDV(MotifRDV.CONSULTATION);
        rdvResponse.setNotes("Test appointment");
        rdvResponse.setStatutRDV(StatutRDV.CONFIRME);
        rdvResponse.setPrenom("John");
        rdvResponse.setNom("Doe");
        rdvResponse.setCin("12345678");
        rdvResponse.setDateNaissance(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createRendezVous_ValidRequest_ShouldCreateAppointment() {
        // Arrange
        when(rdvRepository.findActiveAppointmentsByPatientAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        doAnswer(invocation -> {
            RendezVous rdv = invocation.getArgument(0);
            rdv.setId(1L); // Simulate JPA setting the ID
            return rdv;
        }).when(rdvRepository).save(any(RendezVous.class));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = rdvService.createRendezVous(rdvRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getPrenom());
        assertEquals("Doe", result.getNom());
        verify(rdvRepository).save(any(RendezVous.class));
        verify(patientClient).getPatientById(1L);
    }

    @Test
    void createRendezVous_StartTimeAfterEndTime_ShouldThrowException() {
        // Arrange
        rdvRequest.setHeure_debut(LocalTime.of(11, 0));
        rdvRequest.setHeure_fin(LocalTime.of(10, 0));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.createRendezVous(rdvRequest));
        assertEquals("La date de début doit être avant la date de fin.", exception.getMessage());
    }

    @Test
    void createRendezVous_PastDate_ShouldThrowException() {
        // Arrange
        rdvRequest.setDate(LocalDate.now().minusDays(1));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.createRendezVous(rdvRequest));
        assertEquals("Impossible de prendre un rendez-vous à une date passée.", exception.getMessage());
    }

    @Test
    void createRendezVous_TodayPastTime_ShouldThrowException() {
        // Arrange
        rdvRequest.setDate(LocalDate.now());
        rdvRequest.setHeure_debut(LocalTime.now().minusHours(1));
        rdvRequest.setHeure_fin(LocalTime.now().plusHours(1)); // Make sure end time is after start time

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.createRendezVous(rdvRequest));
        assertEquals("Impossible de prendre un rendez-vous à une heure passée.", exception.getMessage());
    }

    @Test
    void createRendezVous_PatientAlreadyHasAppointment_ShouldThrowException() {
        // Arrange
        when(rdvRepository.findActiveAppointmentsByPatientAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(rendezVous));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.createRendezVous(rdvRequest));
        assertEquals("Ce patient a déjà un rendez-vous programmé pour cette date.", exception.getMessage());
    }

    @Test
    void updateRendezVous_ValidRequest_ShouldUpdateAppointment() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setDate(LocalDate.now().plusDays(2));
        updateDto.setHeure_debut(LocalTime.of(14, 0));
        updateDto.setHeure_fin(LocalTime.of(15, 0));
        updateDto.setMotifRDV(MotifRDV.CONTROL);
        updateDto.setNotes("Updated notes");

        when(rdvRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        doAnswer(invocation -> invocation.getArgument(0)).when(rdvRepository).save(any(RendezVous.class));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = rdvService.updateRendezVous(1L, updateDto);

        // Assert
        assertNotNull(result);
        verify(rdvRepository).findById(1L);
        verify(rdvRepository).save(rendezVous);
        verify(patientClient).getPatientById(1L);
    }

    @Test
    void updateRendezVous_AppointmentNotFound_ShouldThrowException() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        when(rdvRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rdvService.updateRendezVous(1L, updateDto));
        assertTrue(exception.getMessage().contains("RendezVous not found with id: 1"));
    }

    @Test
    void deleteRendezVous_ValidId_ShouldDeleteAppointment() {
        // Act
        rdvService.deleteRendezVous(1L);

        // Assert
        verify(rdvRepository).deleteById(1L);
    }

    @Test
    void getRendezVousById_ValidId_ShouldReturnAppointment() {
        // Arrange
        when(rdvRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = rdvService.getRendezVousById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(rdvRepository).findById(1L);
        verify(patientClient).getPatientById(1L);
    }

    @Test
    void getRendezVousById_AppointmentNotFound_ShouldThrowException() {
        // Arrange
        when(rdvRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rdvService.getRendezVousById(1L));
        assertTrue(exception.getMessage().contains("RendezVous not found with id: 1"));
    }

    @Test
    void getTodayRendezVousForPatient_ValidPatient_ShouldReturnAppointment() {
        // Arrange
        List<StatutRDV> validStatuts = Arrays.asList(StatutRDV.CONFIRME, StatutRDV.EN_CONSULTATION);
        when(rdvRepository.findFirstValidToday(1L, LocalDate.now(), validStatuts))
                .thenReturn(Optional.of(rendezVous));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = rdvService.getTodayRendezVousForPatient(1L);

        // Assert
        assertNotNull(result);
        verify(rdvRepository).findFirstValidToday(1L, LocalDate.now(), validStatuts);
    }

    @Test
    void getTodayRendezVousForPatient_NoAppointment_ShouldThrowException() {
        // Arrange
        List<StatutRDV> validStatuts = Arrays.asList(StatutRDV.CONFIRME, StatutRDV.EN_CONSULTATION);
        when(rdvRepository.findFirstValidToday(1L, LocalDate.now(), validStatuts))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rdvService.getTodayRendezVousForPatient(1L));
        assertTrue(exception.getMessage().contains("Aucun rendez-vous valide aujourd'hui pour le patient: 1"));
    }

    @Test
    void updateStatusRendezVous_ValidTransition_ShouldUpdateStatus() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.CONFIRME);
        when(rdvRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        doAnswer(invocation -> invocation.getArgument(0)).when(rdvRepository).save(any(RendezVous.class));
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        RDVResponse result = rdvService.updateStatusRendezVous(1L, StatutRDV.EN_CONSULTATION);

        // Assert
        assertNotNull(result);
        verify(rdvRepository).save(rendezVous);
    }

    @Test
    void updateStatusRendezVous_InvalidTransitionFromAnnule_ShouldThrowException() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.ANNULE);
        when(rdvRepository.findById(1L)).thenReturn(Optional.of(rendezVous));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.updateStatusRendezVous(1L, StatutRDV.CONFIRME));
        assertEquals("Le statut ne peut pas être modifié.", exception.getMessage());
    }

    @Test
    void updateStatusRendezVous_InvalidTransitionFromConsultation_ShouldThrowException() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.EN_CONSULTATION);
        when(rdvRepository.findById(1L)).thenReturn(Optional.of(rendezVous));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.updateStatusRendezVous(1L, StatutRDV.CONFIRME));
        assertEquals("Le statut ne peut être changé que vers TERMINE .", exception.getMessage());
    }

    @Test
    void updateStatusRendezVous_InvalidTransitionFromConfirmeToTermine_ShouldThrowException() {
        // Arrange
        rendezVous.setStatutRDV(StatutRDV.CONFIRME);
        when(rdvRepository.findById(1L)).thenReturn(Optional.of(rendezVous));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rdvService.updateStatusRendezVous(1L, StatutRDV.TERMINE));
        assertEquals("Le statut ne peut pas être changé directement de CONFIRME à TERMINE.", exception.getMessage());
    }

    @Test
    void getAppointmentsByDate_ValidDate_ShouldReturnAppointments() {
        // Arrange
        List<RendezVous> appointments = Arrays.asList(rendezVous);
        when(rdvRepository.findByDateAndCabinetId(LocalDate.now(), 1L)).thenReturn(appointments);
        when(patientClient.getPatientById(1L)).thenReturn(patientInfo);

        // Act
        List<RDVResponse> result = rdvService.getAppointmentsByDate(LocalDate.now(), 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rdvRepository).findByDateAndCabinetId(LocalDate.now(), 1L);
    }
}