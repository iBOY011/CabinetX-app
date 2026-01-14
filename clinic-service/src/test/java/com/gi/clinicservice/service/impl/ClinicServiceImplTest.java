package com.gi.clinicservice.service.impl;

import com.gi.clinicservice.exception.ResourceNotFoundException;
import com.gi.clinicservice.mapper.ClinicMapper;
import com.gi.clinicservice.model.dto.ClinicDTO;
import com.gi.clinicservice.model.entity.Clinic;
import com.gi.clinicservice.model.enums.ClinicStatus;
import com.gi.clinicservice.repository.ClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicServiceImpl Unit Tests")
class ClinicServiceImplTest {

    @Mock
    private ClinicRepository repository;

    @Mock
    private ClinicMapper mapper;

    @InjectMocks
    private ClinicServiceImpl clinicService;

    private Clinic clinic;
    private ClinicDTO clinicDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        clinic = new Clinic();
        clinic.setId(1L);
        clinic.setName("Cabinet Médical Test");
        clinic.setSpecialty("Médecine Générale");
        clinic.setPhone("0612345678");
        clinic.setAddress("123 Rue Test, Casablanca");
        clinic.setLogoUrl("https://example.com/logo.png");
        clinic.setStatus(ClinicStatus.ACTIVE);
        clinic.setServiceEndDate(LocalDate.now().plusYears(1));

        clinicDTO = new ClinicDTO();
        clinicDTO.setId(1L);
        clinicDTO.setName("Cabinet Médical Test");
        clinicDTO.setSpecialty("Médecine Générale");
        clinicDTO.setPhone("0612345678");
        clinicDTO.setAddress("123 Rue Test, Casablanca");
        clinicDTO.setLogoUrl("https://example.com/logo.png");
        clinicDTO.setStatus(ClinicStatus.ACTIVE);
        clinicDTO.setServiceEndDate(LocalDate.now().plusYears(1));
    }

    @Test
    @DisplayName("Should create clinic successfully")
    void testCreateClinic_Success() {
        // Given
        when(mapper.toEntity(any(ClinicDTO.class))).thenReturn(clinic);
        when(repository.save(any(Clinic.class))).thenReturn(clinic);
        when(mapper.toResponse(any(Clinic.class))).thenReturn(clinicDTO);

        // When
        ClinicDTO result = clinicService.createClinic(clinicDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(clinicDTO.getName());
        assertThat(result.getSpecialty()).isEqualTo(clinicDTO.getSpecialty());

        verify(mapper).toEntity(clinicDTO);
        verify(repository).save(any(Clinic.class));
        verify(mapper).toResponse(any(Clinic.class));
    }

    @Test
    @DisplayName("Should update clinic successfully")
    void testUpdateClinic_Success() {
        // Given
        Long clinicId = 1L;
        when(repository.findById(clinicId)).thenReturn(Optional.of(clinic));
        doNothing().when(mapper).updateEntity(any(ClinicDTO.class), any(Clinic.class));
        when(repository.save(any(Clinic.class))).thenReturn(clinic);
        when(mapper.toResponse(any(Clinic.class))).thenReturn(clinicDTO);

        // When
        ClinicDTO result = clinicService.updateClinic(clinicId, clinicDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clinicId);

        verify(repository).findById(clinicId);
        verify(mapper).updateEntity(clinicDTO, clinic);
        verify(repository).save(clinic);
        verify(mapper).toResponse(clinic);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent clinic")
    void testUpdateClinic_NotFound() {
        // Given
        Long clinicId = 999L;
        when(repository.findById(clinicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clinicService.updateClinic(clinicId, clinicDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Clinic not found");

        verify(repository).findById(clinicId);
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should activate clinic successfully")
    void testActivateClinic_Success() {
        // Given
        Long clinicId = 1L;
        clinic.setStatus(ClinicStatus.INACTIVE);
        when(repository.findById(clinicId)).thenReturn(Optional.of(clinic));
        when(repository.save(any(Clinic.class))).thenReturn(clinic);
        when(mapper.toResponse(any(Clinic.class))).thenReturn(clinicDTO);

        // When
        ClinicDTO result = clinicService.activateClinic(clinicId);

        // Then
        assertThat(result).isNotNull();
        assertThat(clinic.getStatus()).isEqualTo(ClinicStatus.ACTIVE);

        verify(repository).findById(clinicId);
        verify(repository).save(clinic);
        verify(mapper).toResponse(clinic);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when activating non-existent clinic")
    void testActivateClinic_NotFound() {
        // Given
        Long clinicId = 999L;
        when(repository.findById(clinicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clinicService.activateClinic(clinicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Clinic not found");

        verify(repository).findById(clinicId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should deactivate clinic successfully")
    void testDeactivateClinic_Success() {
        // Given
        Long clinicId = 1L;
        when(repository.findById(clinicId)).thenReturn(Optional.of(clinic));
        when(repository.save(any(Clinic.class))).thenReturn(clinic);
        when(mapper.toResponse(any(Clinic.class))).thenReturn(clinicDTO);

        // When
        ClinicDTO result = clinicService.deactivateClinic(clinicId);

        // Then
        assertThat(result).isNotNull();
        assertThat(clinic.getStatus()).isEqualTo(ClinicStatus.INACTIVE);

        verify(repository).findById(clinicId);
        verify(repository).save(clinic);
        verify(mapper).toResponse(clinic);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deactivating non-existent clinic")
    void testDeactivateClinic_NotFound() {
        // Given
        Long clinicId = 999L;
        when(repository.findById(clinicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clinicService.deactivateClinic(clinicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Clinic not found");

        verify(repository).findById(clinicId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should find clinic by ID successfully")
    void testFindById_Success() {
        // Given
        Long clinicId = 1L;
        when(repository.findById(clinicId)).thenReturn(Optional.of(clinic));
        when(mapper.toResponse(any(Clinic.class))).thenReturn(clinicDTO);

        // When
        ClinicDTO result = clinicService.findById(clinicId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clinicId);

        verify(repository).findById(clinicId);
        verify(mapper).toResponse(clinic);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when finding non-existent clinic")
    void testFindById_NotFound() {
        // Given
        Long clinicId = 999L;
        when(repository.findById(clinicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clinicService.findById(clinicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Clinic not found");

        verify(repository).findById(clinicId);
        verify(mapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should find all clinics successfully")
    void testFindAll_WithResults() {
        // Given
        Clinic clinic2 = new Clinic();
        clinic2.setId(2L);
        clinic2.setName("Cabinet 2");

        ClinicDTO clinicDTO2 = new ClinicDTO();
        clinicDTO2.setId(2L);
        clinicDTO2.setName("Cabinet 2");

        List<Clinic> clinics = Arrays.asList(clinic, clinic2);

        when(repository.findAll()).thenReturn(clinics);
        when(mapper.toResponse(clinic)).thenReturn(clinicDTO);
        when(mapper.toResponse(clinic2)).thenReturn(clinicDTO2);

        // When
        List<ClinicDTO> results = clinicService.findAll();

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Cabinet Médical Test");
        assertThat(results.get(1).getName()).isEqualTo("Cabinet 2");

        verify(repository).findAll();
        verify(mapper, times(2)).toResponse(any(Clinic.class));
    }

    @Test
    @DisplayName("Should return empty list when no clinics exist")
    void testFindAll_EmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ClinicDTO> results = clinicService.findAll();

        // Then
        assertThat(results).isEmpty();

        verify(repository).findAll();
        verify(mapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should find active clinics successfully")
    void testFindActive_Success() {
        // Given
        List<Clinic> activeClinics = Arrays.asList(clinic);

        when(repository.findByStatus(ClinicStatus.ACTIVE)).thenReturn(activeClinics);
        when(mapper.toResponse(clinic)).thenReturn(clinicDTO);

        // When
        List<ClinicDTO> results = clinicService.findActive();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(ClinicStatus.ACTIVE);

        verify(repository).findByStatus(ClinicStatus.ACTIVE);
        verify(mapper).toResponse(clinic);
    }

    @Test
    @DisplayName("Should find clinics near expiration successfully")
    void testFindNearExpiration_Success() {
        // Given
        int daysBefore = 30;
        LocalDate expirationDate = LocalDate.now().plusDays(daysBefore);

        clinic.setServiceEndDate(LocalDate.now().plusDays(15)); // Expires in 15 days
        List<Clinic> expiringClinics = Arrays.asList(clinic);

        when(repository.findByServiceEndDateBefore(expirationDate)).thenReturn(expiringClinics);
        when(mapper.toResponse(clinic)).thenReturn(clinicDTO);

        // When
        List<ClinicDTO> results = clinicService.findNearExpiration(daysBefore);

        // Then
        assertThat(results).hasSize(1);

        verify(repository).findByServiceEndDateBefore(expirationDate);
        verify(mapper).toResponse(clinic);
    }

    @Test
    @DisplayName("Should return empty list when no clinics near expiration")
    void testFindNearExpiration_EmptyList() {
        // Given
        int daysBefore = 30;
        LocalDate expirationDate = LocalDate.now().plusDays(daysBefore);

        when(repository.findByServiceEndDateBefore(expirationDate)).thenReturn(Collections.emptyList());

        // When
        List<ClinicDTO> results = clinicService.findNearExpiration(daysBefore);

        // Then
        assertThat(results).isEmpty();

        verify(repository).findByServiceEndDateBefore(expirationDate);
        verify(mapper, never()).toResponse(any());
    }
}
