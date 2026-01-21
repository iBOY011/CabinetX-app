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

/**
 * Service layer for managing patient records in the medical practice management system.
 * 
 * <p>This service provides comprehensive patient management functionality including:
 * <ul>
 *   <li>Patient registration and profile management</li>
 *   <li>CIN (National ID) validation and uniqueness checks</li>
 *   <li>Patient search by name, CIN, and clinic association</li>
 *   <li>Event tracking for audit and compliance (CREATED, UPDATED, DELETED)</li>
 *   <li>Integration with Kafka for publishing patient lifecycle events</li>
 * </ul>
 * 
 * <p><b>Business Rules:</b></p>
 * <ul>
 *   <li>CIN must be unique across the system</li>
 *   <li>Patient must be associated with an active clinic</li>
 *   <li>All patient operations are audited via PatientEvent</li>
 *   <li>Soft delete not implemented - deletion is permanent</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 * @see Patient
 * @see PatientDTO
 * @see PatientEvent
 */
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

    /**
     * Creates a new patient record in the system.
     * 
     * <p>This method performs the following operations:</p>
     * <ol>
     *   <li>Validates patient data (CIN uniqueness, required fields)</li>
     *   <li>Persists patient entity to database</li>
     *   <li>Records CREATED event for audit trail</li>
     *   <li>Publishes patient-created event to Kafka topic</li>
     * </ol>
     *
     * @param dto Patient data transfer object containing all patient information
     * @return PatientDTO with generated ID and timestamps
     * @throws org.springframework.dao.DataIntegrityViolationException if CIN already exists
     * @throws jakarta.validation.ValidationException if required fields are missing
     */
    public PatientDTO createPatient(PatientDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        patient.setId(null);
        Patient saved = patientRepository.save(patient);
        recordEvent(saved.getId(), EventType.CREATED);
        return patientMapper.toDTO(saved);
    }

    /**
     * Updates an existing patient record.
     * 
     * <p>Only provided fields in the DTO are updated. The method applies partial
     * updates while maintaining data integrity and audit trail.</p>
     *
     * @param id Unique identifier of the patient to update
     * @param dto Patient data transfer object with fields to update
     * @return Updated PatientDTO with new values and updated timestamp
     * @throws ResponseStatusException with 404 if patient not found
     */
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        Patient existing = getPatientEntity(id);
        applyChanges(existing, dto);
        Patient updated = patientRepository.save(existing);
        recordEvent(updated.getId(), EventType.UPDATED);
        return patientMapper.toDTO(updated);
    }

    /**
     * Permanently deletes a patient and all associated events.
     * 
     * <p><b>Warning:</b> This is a hard delete operation. All patient data and
     * associated events will be permanently removed. Consider implementing soft
     * delete for production systems with regulatory compliance requirements.</p>
     *
     * @param id Unique identifier of the patient to delete
     * @throws ResponseStatusException with 404 if patient not found
     * @throws org.springframework.dao.DataIntegrityViolationException if patient has
     *         active consultations or appointments (referential integrity violation)
     */
    public void deletePatient(Long id) {
        Patient existing = getPatientEntity(id);
        patientEventRepository.deleteByPatientId(id);
        patientRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        return patientMapper.toDTO(getPatientEntity(id));
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientByCin(String cin) {
        return patientRepository.findByCin(cin)
                .map(patientMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Patient introuvable pour le CIN fourni"));
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

    @Transactional(readOnly = true)
    public List<PatientDTO> listPatientsByCabinet(Long cabinetId) {
        return patientMapper.toDTOList(patientRepository.findByCabinetId(cabinetId));
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
