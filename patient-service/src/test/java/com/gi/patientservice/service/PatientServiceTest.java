package com.gi.patientservice.service;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.entities.Adresse;
import com.gi.patientservice.entities.Patient;
import com.gi.patientservice.entities.PatientEvent;
import com.gi.patientservice.enums.EventType;
import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import com.gi.patientservice.mappers.PatientMapper;
import com.gi.patientservice.repository.PatientEventRepository;
import com.gi.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientEventRepository patientEventRepository;
    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private PatientDTO dto;
    private Patient entity;

    @BeforeEach
    void setUp() {
        dto = buildDto(1L, "AB123456", "Doe", "John");
        entity = buildEntity(1L, "AB123456", "Doe", "John");
    }

    @Test
    void createPatient_savesEntityAndRecordsEvent() {
        Patient entityToSave = buildEntity(null, dto.getCin(), dto.getNom(), dto.getPrenom());
        when(patientMapper.toEntity(dto)).thenReturn(entityToSave);
        Patient saved = buildEntity(42L, dto.getCin(), dto.getNom(), dto.getPrenom());
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);
        PatientDTO expectedDto = buildDto(42L, dto.getCin(), dto.getNom(), dto.getPrenom());
        when(patientMapper.toDTO(saved)).thenReturn(expectedDto);

        PatientDTO result = patientService.createPatient(dto);

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        assertThat(patientCaptor.getValue().getId()).isNull();

        ArgumentCaptor<PatientEvent> eventCaptor = ArgumentCaptor.forClass(PatientEvent.class);
        verify(patientEventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getPatientId()).isEqualTo(42L);
        assertThat(eventCaptor.getValue().getType()).isEqualTo(EventType.CREATED);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void updatePatient_updatesFieldsAndRecordsEvent() {
        Patient existing = buildEntity(5L, "CD765432", "Old", "Name");
        when(patientRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PatientDTO updatedDto = buildDto(5L, "CD765432", "New", "Name");
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(updatedDto);

        PatientDTO result = patientService.updatePatient(5L, updatedDto);

        ArgumentCaptor<Patient> savedCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(savedCaptor.capture());
        Patient saved = savedCaptor.getValue();
        assertThat(saved.getNom()).isEqualTo("New");
        assertThat(saved.getCin()).isEqualTo("CD765432");

        ArgumentCaptor<PatientEvent> eventCaptor = ArgumentCaptor.forClass(PatientEvent.class);
        verify(patientEventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getPatientId()).isEqualTo(5L);
        assertThat(eventCaptor.getValue().getType()).isEqualTo(EventType.UPDATED);

        assertThat(result).isEqualTo(updatedDto);
    }

    @Test
    void updatePatient_throwsWhenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updatePatient(99L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(patientRepository, never()).save(any());
        verifyNoInteractions(patientEventRepository);
    }

    @Test
    void deletePatient_deletesEventsAndEntity() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(entity));

        patientService.deletePatient(3L);

        verify(patientEventRepository).deleteByPatientId(3L);
        verify(patientRepository).delete(entity);
    }

    @Test
    void getPatientById_returnsMappedDto() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(patientMapper.toDTO(entity)).thenReturn(dto);

        PatientDTO result = patientService.getPatientById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getPatientByCin_returnsDtoWhenFound() {
        when(patientRepository.findByCin("AB123456")).thenReturn(Optional.of(entity));
        when(patientMapper.toDTO(entity)).thenReturn(dto);

        PatientDTO result = patientService.getPatientByCin("AB123456");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getPatientByCin_throwsWhenMissing() {
        when(patientRepository.findByCin("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientByCin("UNKNOWN"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void searchPatients_usesNomAndPrenomWhenBothProvided() {
        when(patientRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase("do", "jo"))
                .thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.searchPatients("do", "jo");

        assertThat(result).containsExactly(dto);
        verify(patientRepository, times(1)).findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase("do", "jo");
        verify(patientRepository, never()).findByNomContainingIgnoreCase(any());
        verify(patientRepository, never()).findByPrenomContainingIgnoreCase(any());
        verify(patientRepository, never()).findAll();
    }

    @Test
    void searchPatients_usesOnlyNomWhenPrenomBlank() {
        when(patientRepository.findByNomContainingIgnoreCase("do"))
                .thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.searchPatients("do", "  ");

        assertThat(result).containsExactly(dto);
        verify(patientRepository).findByNomContainingIgnoreCase("do");
        verify(patientRepository, never()).findByPrenomContainingIgnoreCase(any());
        verify(patientRepository, never()).findAll();
    }

    @Test
    void searchPatients_usesOnlyPrenomWhenNomBlank() {
        when(patientRepository.findByPrenomContainingIgnoreCase("jo"))
                .thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.searchPatients("  ", "jo");

        assertThat(result).containsExactly(dto);
        verify(patientRepository).findByPrenomContainingIgnoreCase("jo");
        verify(patientRepository, never()).findAll();
    }

    @Test
    void searchPatients_returnsAllWhenNoFilters() {
        when(patientRepository.findAll()).thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.searchPatients(null, null);

        assertThat(result).containsExactly(dto);
        verify(patientRepository).findAll();
    }

    @Test
    void listPatients_returnsAllMapped() {
        when(patientRepository.findAll()).thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.listPatients();

        assertThat(result).containsExactly(dto);
    }

    @Test
    void listPatientsByCabinet_filtersByCabinet() {
        when(patientRepository.findByCabinetId(9L)).thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.listPatientsByCabinet(9L);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void getPatientById_throwsWhenNotFound() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deletePatient_throwsWhenNotFound() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.deletePatient(999L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(patientRepository, never()).delete(any());
        verify(patientEventRepository, never()).deleteByPatientId(any());
    }

    @Test
    void searchPatients_withEmptyStrings_returnsAll() {
        when(patientRepository.findAll()).thenReturn(List.of(entity));
        when(patientMapper.toDTOList(List.of(entity))).thenReturn(List.of(dto));

        List<PatientDTO> result = patientService.searchPatients("", "");

        assertThat(result).containsExactly(dto);
        verify(patientRepository).findAll();
    }

    @Test
    void listPatients_returnsEmptyListWhenNoPatients() {
        when(patientRepository.findAll()).thenReturn(List.of());
        when(patientMapper.toDTOList(List.of())).thenReturn(List.of());

        List<PatientDTO> result = patientService.listPatients();

        assertThat(result).isEmpty();
    }

    @Test
    void listPatientsByCabinet_returnsEmptyListWhenNoMatch() {
        when(patientRepository.findByCabinetId(999L)).thenReturn(List.of());
        when(patientMapper.toDTOList(List.of())).thenReturn(List.of());

        List<PatientDTO> result = patientService.listPatientsByCabinet(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void updatePatient_appliesAllFieldChanges() {
        Patient existing = buildEntity(7L, "OLD123", "OldNom", "OldPrenom");
        when(patientRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientDTO updateDto = PatientDTO.builder()
                .cin("NEW999")
                .nom("NewNom")
                .prenom("NewPrenom")
                .dateNaissance(LocalDate.of(2000, 12, 25))
                .sexe(Sexe.FEMININ)
                .numTel("0700000000")
                .typeMutuelle(TypeMutuelle.PRIVEE)
                .cabinetId(99L)
                .adresse(Adresse.builder().ville("Tanger").build())
                .build();
        when(patientMapper.toDTO(any(Patient.class))).thenReturn(updateDto);

        patientService.updatePatient(7L, updateDto);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        Patient saved = captor.getValue();

        assertThat(saved.getCin()).isEqualTo("NEW999");
        assertThat(saved.getNom()).isEqualTo("NewNom");
        assertThat(saved.getPrenom()).isEqualTo("NewPrenom");
        assertThat(saved.getDateNaissance()).isEqualTo(LocalDate.of(2000, 12, 25));
        assertThat(saved.getSexe()).isEqualTo(Sexe.FEMININ);
        assertThat(saved.getNumTel()).isEqualTo("0700000000");
        assertThat(saved.getTypeMutuelle()).isEqualTo(TypeMutuelle.PRIVEE);
        assertThat(saved.getCabinetId()).isEqualTo(99L);
        assertThat(saved.getAdresse().getVille()).isEqualTo("Tanger");
    }

    private PatientDTO buildDto(Long id, String cin, String nom, String prenom) {
        return PatientDTO.builder()
                .id(id)
                .cin(cin)
                .nom(nom)
                .prenom(prenom)
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe(Sexe.MASCULIN)
                .numTel("0612345678")
                .typeMutuelle(TypeMutuelle.CNSS)
                .cabinetId(1L)
                .adresse(Adresse.builder().ville("Paris").build())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Patient buildEntity(Long id, String cin, String nom, String prenom) {
        return Patient.builder()
                .id(id)
                .cin(cin)
                .nom(nom)
                .prenom(prenom)
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe(Sexe.MASCULIN)
                .numTel("0612345678")
                .typeMutuelle(TypeMutuelle.CNSS)
                .cabinetId(1L)
                .adresse(Adresse.builder().ville("Paris").build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
