package com.gi.patientservice.web;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.entities.Adresse;
import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import com.gi.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private PatientDTO dto;

    @BeforeEach
    void setUp() {
        dto = PatientDTO.builder()
                .id(1L)
                .cin("AB123456")
                .nom("Doe")
                .prenom("John")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe(Sexe.MASCULIN)
                .numTel("0612345678")
                .typeMutuelle(TypeMutuelle.CNSS)
                .cabinetId(5L)
                .adresse(Adresse.builder().ville("Rabat").build())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPatient_delegatesToService() {
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(dto);

        PatientDTO result = patientController.createPatient(dto);

        assertThat(result).isEqualTo(dto);
        verify(patientService).createPatient(dto);
    }

    @Test
    void updatePatient_delegatesToService() {
        PatientDTO updated = PatientDTO.builder()
                .id(1L)
                .cin("AB123456")
                .nom("UpdatedName")
                .prenom("John")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe(Sexe.MASCULIN)
                .numTel("0612345678")
                .typeMutuelle(TypeMutuelle.CNSS)
                .cabinetId(5L)
                .build();
        when(patientService.updatePatient(eq(1L), any(PatientDTO.class))).thenReturn(updated);

        PatientDTO result = patientController.updatePatient(1L, updated);

        assertThat(result.getNom()).isEqualTo("UpdatedName");
        verify(patientService).updatePatient(1L, updated);
    }

    @Test
    void deletePatient_delegatesToService() {
        doNothing().when(patientService).deletePatient(1L);

        patientController.deletePatient(1L);

        verify(patientService).deletePatient(1L);
    }

    @Test
    void getPatient_returnsDto() {
        when(patientService.getPatientById(1L)).thenReturn(dto);

        PatientDTO result = patientController.getPatient(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getPatient_propagatesException() {
        when(patientService.getPatientById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));

        assertThatThrownBy(() -> patientController.getPatient(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByCin_returnsDto() {
        when(patientService.getPatientByCin("AB123456")).thenReturn(dto);

        PatientDTO result = patientController.getByCin("AB123456");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void listPatients_withoutFilters_returnsAll() {
        when(patientService.listPatients()).thenReturn(List.of(dto));

        List<PatientDTO> result = patientController.listPatients(null, null);

        assertThat(result).containsExactly(dto);
        verify(patientService).listPatients();
    }

    @Test
    void listPatients_withNomFilter_callsSearch() {
        when(patientService.searchPatients("Doe", null)).thenReturn(List.of(dto));

        List<PatientDTO> result = patientController.listPatients("Doe", null);

        assertThat(result).containsExactly(dto);
        verify(patientService).searchPatients("Doe", null);
    }

    @Test
    void listPatients_withPrenomFilter_callsSearch() {
        when(patientService.searchPatients(null, "John")).thenReturn(List.of(dto));

        List<PatientDTO> result = patientController.listPatients(null, "John");

        assertThat(result).containsExactly(dto);
        verify(patientService).searchPatients(null, "John");
    }

    @Test
    void listPatients_withBothFilters_callsSearch() {
        when(patientService.searchPatients("Doe", "John")).thenReturn(List.of(dto));

        List<PatientDTO> result = patientController.listPatients("Doe", "John");

        assertThat(result).containsExactly(dto);
        verify(patientService).searchPatients("Doe", "John");
    }

    @Test
    void listPatientsByCabinet_filtersByCabinetId() {
        when(patientService.listPatientsByCabinet(5L)).thenReturn(List.of(dto));

        List<PatientDTO> result = patientController.listPatientsByCabinet(5L);

        assertThat(result).containsExactly(dto);
        verify(patientService).listPatientsByCabinet(5L);
    }

    @Test
    void listPatientsByCabinet_returnsEmptyWhenNoneFound() {
        when(patientService.listPatientsByCabinet(999L)).thenReturn(List.of());

        List<PatientDTO> result = patientController.listPatientsByCabinet(999L);

        assertThat(result).isEmpty();
    }
}
