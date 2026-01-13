package com.gi.medicationservice.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gi.medicationservice.exception.ResourceNotFoundException;
import com.gi.medicationservice.mapper.MedicationMapper;
import com.gi.medicationservice.model.dto.MedicationDTO;
import com.gi.medicationservice.model.entity.Medication;
import com.gi.medicationservice.repository.MedicationRepository;

@ExtendWith(MockitoExtension.class)
class MedicationServiceImplTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private MedicationMapper medicationMapper;

    @InjectMocks
    private MedicationServiceImpl medicationService;

    private Medication medication;
    private MedicationDTO medicationDTO;

    @BeforeEach
    void setUp() {
        medication = new Medication();
        medication.setId(1L);
        medication.setCommercialName("Aspirin");
        medication.setDci("Acetylsalicylic Acid");
        medication.setDosage("500mg");
        medication.setForm("Tablet");

        medicationDTO = new MedicationDTO();
        medicationDTO.setId(1L);
        medicationDTO.setCommercialName("Aspirin");
        medicationDTO.setDci("Acetylsalicylic Acid");
        medicationDTO.setDosage("500mg");
        medicationDTO.setForm("Tablet");
    }

    @Test
    void createMedication_ShouldReturnCreatedMedicationDTO() {
        // Arrange
        when(medicationMapper.toEntity(medicationDTO)).thenReturn(medication);
        when(medicationRepository.save(medication)).thenReturn(medication);
        when(medicationMapper.toDTO(medication)).thenReturn(medicationDTO);

        // Act
        MedicationDTO result = medicationService.createMedication(medicationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(medicationDTO.getCommercialName(), result.getCommercialName());
        verify(medicationMapper).toEntity(medicationDTO);
        verify(medicationRepository).save(medication);
        verify(medicationMapper).toDTO(medication);
    }

    @Test
    void updateMedication_ShouldReturnUpdatedMedicationDTO_WhenMedicationExists() {
        // Arrange
        Long id = 1L;
        MedicationDTO updateDTO = new MedicationDTO();
        updateDTO.setCommercialName("Updated Aspirin");
        updateDTO.setDci("Updated DCI");
        updateDTO.setDosage("1000mg");
        updateDTO.setForm("Capsule");

        when(medicationRepository.findById(id)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any(Medication.class))).thenReturn(medication);
        when(medicationMapper.toDTO(medication)).thenReturn(updateDTO);

        // Act
        MedicationDTO result = medicationService.updateMedication(id, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updateDTO.getCommercialName(), result.getCommercialName());
        verify(medicationRepository).findById(id);
        verify(medicationRepository).save(medication);
        verify(medicationMapper).toDTO(medication);
    }

    @Test
    void updateMedication_ShouldThrowResourceNotFoundException_WhenMedicationNotFound() {
        // Arrange
        Long id = 1L;
        when(medicationRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> medicationService.updateMedication(id, medicationDTO));
        assertEquals("Medication not found with id: " + id, exception.getMessage());
        verify(medicationRepository).findById(id);
        verify(medicationRepository, never()).save(any(Medication.class));
    }

    @Test
    void deleteMedication_ShouldDeleteMedication_WhenMedicationExists() {
        // Arrange
        Long id = 1L;
        when(medicationRepository.existsById(id)).thenReturn(true);

        // Act
        medicationService.deleteMedication(id);

        // Assert
        verify(medicationRepository).existsById(id);
        verify(medicationRepository).deleteById(id);
    }

    @Test
    void deleteMedication_ShouldThrowResourceNotFoundException_WhenMedicationNotFound() {
        // Arrange
        Long id = 1L;
        when(medicationRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> medicationService.deleteMedication(id));
        assertEquals("Medication not found with id: " + id, exception.getMessage());
        verify(medicationRepository).existsById(id);
        verify(medicationRepository, never()).deleteById(id);
    }

    @Test
    void findById_ShouldReturnMedicationDTO_WhenMedicationExists() {
        // Arrange
        Long id = 1L;
        when(medicationRepository.findById(id)).thenReturn(Optional.of(medication));
        when(medicationMapper.toDTO(medication)).thenReturn(medicationDTO);

        // Act
        MedicationDTO result = medicationService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(medicationDTO.getId(), result.getId());
        verify(medicationRepository).findById(id);
        verify(medicationMapper).toDTO(medication);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenMedicationNotFound() {
        // Arrange
        Long id = 1L;
        when(medicationRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> medicationService.findById(id));
        assertEquals("Medication not found with id: " + id, exception.getMessage());
        verify(medicationRepository).findById(id);
        verify(medicationMapper, never()).toDTO(any(Medication.class));
    }

    @Test
    void autocomplete_ShouldReturnListOfMedicationDTOs() {
        // Arrange
        String term = "asp";
        List<Medication> medications = Arrays.asList(medication);
        List<MedicationDTO> expectedDTOs = Arrays.asList(medicationDTO);

        when(medicationRepository.findByCommercialNameContainingIgnoreCase(term)).thenReturn(medications);
        when(medicationRepository.findByDciContainingIgnoreCase(term)).thenReturn(Arrays.asList());
        when(medicationMapper.toDTO(medication)).thenReturn(medicationDTO);

        // Act
        List<MedicationDTO> result = medicationService.autocomplete(term);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDTOs.get(0).getCommercialName(), result.get(0).getCommercialName());
        verify(medicationRepository).findByCommercialNameContainingIgnoreCase(term);
        verify(medicationRepository).findByDciContainingIgnoreCase(term);
        verify(medicationMapper).toDTO(medication);
    }

    @Test
    void findAll_ShouldReturnListOfAllMedicationDTOs() {
        // Arrange
        List<Medication> medications = Arrays.asList(medication);
        List<MedicationDTO> expectedDTOs = Arrays.asList(medicationDTO);

        when(medicationRepository.findAll()).thenReturn(medications);
        when(medicationMapper.toDTO(medication)).thenReturn(medicationDTO);

        // Act
        List<MedicationDTO> result = medicationService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDTOs.get(0).getCommercialName(), result.get(0).getCommercialName());
        verify(medicationRepository).findAll();
        verify(medicationMapper).toDTO(medication);
    }

    @Test
    void importFromFile_ShouldReturnZero() {
        // Arrange
        byte[] file = new byte[]{1, 2, 3};

        // Act
        int result = medicationService.importFromFile(file);

        // Assert
        assertEquals(0, result);
    }
}