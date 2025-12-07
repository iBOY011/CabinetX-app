package com.gi.patientservice.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.entities.Patient;
import com.gi.patientservice.entities.PatientEvent;
import com.gi.patientservice.enums.EventType;
import com.gi.patientservice.mappers.PatientMapper;
import com.gi.patientservice.repository.PatientEventRepository;
import com.gi.patientservice.repository.PatientRepository;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientEventRepository patientEventRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository,
                          PatientEventRepository patientEventRepository,
                          PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientEventRepository = patientEventRepository;
        this.patientMapper = patientMapper;
    }

    public PatientDTO createPatient(PatientDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        patient.setId(null);
        Patient saved = patientRepository.save(patient);
        recordEvent(saved.getId(), EventType.CREATED);
        return patientMapper.toDTO(saved);
    }

    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        Patient existing = getPatientEntity(id);
        applyChanges(existing, dto);
        Patient updated = patientRepository.save(existing);
        recordEvent(updated.getId(), EventType.UPDATED);
        return patientMapper.toDTO(updated);
    }

    public void deletePatient(Long id) {
        Patient existing = getPatientEntity(id);
        patientRepository.delete(existing);
        recordEvent(id, EventType.DELETED);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        return patientMapper.toDTO(getPatientEntity(id));
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientByCin(String cin) {
        return patientRepository.findByCin(cin)
                .map(patientMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable pour le CIN fourni"));
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatients(String nom, String prenom) {
        String nomFilter = blankToNull(nom);
        String prenomFilter = blankToNull(prenom);

        List<Patient> patients;
        if (nomFilter != null && prenomFilter != null) {
            patients = patientRepository
                    .findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(nomFilter, prenomFilter);
        } else if (nomFilter != null) {
            patients = patientRepository.findByNomContainingIgnoreCase(nomFilter);
        } else if (prenomFilter != null) {
            patients = patientRepository.findByPrenomContainingIgnoreCase(prenomFilter);
        } else {
            patients = patientRepository.findAll();
        }

        return patientMapper.toDTOList(patients);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> listPatients() {
        return patientMapper.toDTOList(patientRepository.findAll());
    }

    private Patient getPatientEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));
    }

    private void applyChanges(Patient existing, PatientDTO dto) {
        existing.setCin(dto.getCin());
        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setDateNaissance(dto.getDateNaissance());
        existing.setSexe(dto.getSexe());
        existing.setNumTel(dto.getNumTel());
        existing.setTypeMutuelle(dto.getTypeMutuelle());
        existing.setCabinetId(dto.getCabinetId());
        existing.setAdresse(dto.getAdresse());
    }

    private void recordEvent(Long patientId, EventType type) {
        patientEventRepository.save(PatientEvent.builder()
                .patientId(patientId)
                .type(type)
                .build());
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
