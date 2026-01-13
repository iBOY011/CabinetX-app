package com.gi.appointmentservice.Mapper;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;
import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.MotifRDV;
import com.gi.appointmentservice.Model.Enum.StatutRDV;

class RDVMapperTest {

    private RDVRequest rdvRequest;
    private RendezVous rendezVous;
    private PatientInfoDTO patientInfo;
    private RDVResponse expectedResponse;

    @BeforeEach
    void setUp() {
        rdvRequest = new RDVRequest();
        rdvRequest.setPatientId(1L);
        rdvRequest.setCabinetId(1L);
        rdvRequest.setDate(LocalDate.of(2026, 1, 15));
        rdvRequest.setHeure_debut(LocalTime.of(10, 0));
        rdvRequest.setHeure_fin(LocalTime.of(11, 0));
        rdvRequest.setMotifRDV(MotifRDV.CONSULTATION);
        rdvRequest.setNotes("Test appointment");

        rendezVous = new RendezVous();
        rendezVous.setId(1L);
        rendezVous.setPatientId(1L);
        rendezVous.setCabinetId(1L);
        rendezVous.setDate(LocalDate.of(2026, 1, 15));
        rendezVous.setHeure_debut(LocalTime.of(10, 0));
        rendezVous.setHeure_fin(LocalTime.of(11, 0));
        rendezVous.setMotifRDV(MotifRDV.CONSULTATION);
        rendezVous.setStatutRDV(StatutRDV.CONFIRME);
        rendezVous.setNotes("Test appointment");
        rendezVous.setQueuePosition(1);

        patientInfo = new PatientInfoDTO();
        patientInfo.setPrenom("John");
        patientInfo.setNom("Doe");
        patientInfo.setCin("12345678");
        patientInfo.setDateNaissance(LocalDate.of(1990, 1, 1));

        expectedResponse = new RDVResponse();
        expectedResponse.setId(1L);
        expectedResponse.setPatientId(1L);
        expectedResponse.setCabinetId(1L);
        expectedResponse.setDate(LocalDate.of(2026, 1, 15));
        expectedResponse.setHeure_debut(LocalTime.of(10, 0));
        expectedResponse.setHeure_fin(LocalTime.of(11, 0));
        expectedResponse.setMotifRDV(MotifRDV.CONSULTATION);
        expectedResponse.setStatutRDV(StatutRDV.CONFIRME);
        expectedResponse.setNotes("Test appointment");
        expectedResponse.setQueuePosition(1);
        expectedResponse.setPrenom("John");
        expectedResponse.setNom("Doe");
        expectedResponse.setCin("12345678");
        expectedResponse.setDateNaissance(LocalDate.of(1990, 1, 1));
    }

    @Test
    void toEntity_ValidRequest_ShouldMapToEntity() {
        // Act
        RendezVous result = RDVMapper.toEntity(rdvRequest);

        // Assert
        assertNotNull(result);
        assertEquals(rdvRequest.getPatientId(), result.getPatientId());
        assertEquals(rdvRequest.getCabinetId(), result.getCabinetId());
        assertEquals(rdvRequest.getDate(), result.getDate());
        assertEquals(rdvRequest.getHeure_debut(), result.getHeure_debut());
        assertEquals(rdvRequest.getHeure_fin(), result.getHeure_fin());
        assertEquals(rdvRequest.getMotifRDV(), result.getMotifRDV());
        assertEquals(rdvRequest.getNotes(), result.getNotes());
        assertEquals(StatutRDV.CONFIRME, result.getStatutRDV());
    }

    @Test
    void toResponse_ValidEntityAndPatient_ShouldMapToResponse() {
        // Act
        RDVResponse result = RDVMapper.toResponse(rendezVous, patientInfo);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getPatientId(), result.getPatientId());
        assertEquals(expectedResponse.getCabinetId(), result.getCabinetId());
        assertEquals(expectedResponse.getDate(), result.getDate());
        assertEquals(expectedResponse.getHeure_debut(), result.getHeure_debut());
        assertEquals(expectedResponse.getHeure_fin(), result.getHeure_fin());
        assertEquals(expectedResponse.getMotifRDV(), result.getMotifRDV());
        assertEquals(expectedResponse.getStatutRDV(), result.getStatutRDV());
        assertEquals(expectedResponse.getNotes(), result.getNotes());
        assertEquals(expectedResponse.getQueuePosition(), result.getQueuePosition());
        assertEquals(expectedResponse.getPrenom(), result.getPrenom());
        assertEquals(expectedResponse.getNom(), result.getNom());
        assertEquals(expectedResponse.getCin(), result.getCin());
        assertEquals(expectedResponse.getDateNaissance(), result.getDateNaissance());
    }

    @Test
    void toEntity_NullNotes_ShouldHandleNullNotes() {
        // Arrange
        rdvRequest.setNotes(null);

        // Act
        RendezVous result = RDVMapper.toEntity(rdvRequest);

        // Assert
        assertNull(result.getNotes());
    }

    @Test
    void toResponse_NullQueuePosition_ShouldHandleNullQueuePosition() {
        // Arrange
        rendezVous.setQueuePosition(null);

        // Act
        RDVResponse result = RDVMapper.toResponse(rendezVous, patientInfo);

        // Assert
        assertNull(result.getQueuePosition());
    }
}