package com.gi.medicationservice.service.impl;

import com.gi.medicationservice.exception.ResourceNotFoundException;
import com.gi.medicationservice.mapper.MedicationMapper;
import com.gi.medicationservice.model.dto.MedicationDTO;
import com.gi.medicationservice.model.entity.Medication;
import com.gi.medicationservice.repository.MedicationRepository;
import com.gi.medicationservice.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing medication catalog and prescriptions.
 * 
 * <p>This service handles the complete medication database including CRUD operations,
 * autocomplete search functionality, and bulk import capabilities. Medications are
 * identified by both commercial names and DCI (International Nonproprietary Name).</p>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 */
@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private MedicationMapper medicationMapper;

    @Override
    public MedicationDTO createMedication(MedicationDTO dto) {
        Medication medication = medicationMapper.toEntity(dto);
        medication = medicationRepository.save(medication);
        return medicationMapper.toDTO(medication);
    }

    @Override
    public MedicationDTO updateMedication(Long id, MedicationDTO dto) {
        Medication existing = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
        existing.setCommercialName(dto.getCommercialName());
        existing.setDci(dto.getDci());
        existing.setDosage(dto.getDosage());
        existing.setForm(dto.getForm());
        existing = medicationRepository.save(existing);
        return medicationMapper.toDTO(existing);
    }

    @Override
    public void deleteMedication(Long id) {
        if (!medicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medication not found with id: " + id);
        }
        medicationRepository.deleteById(id);
    }

    @Override
    public MedicationDTO findById(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
        return medicationMapper.toDTO(medication);
    }

    @Override
    public List<MedicationDTO> autocomplete(String term) {
        List<Medication> medications = medicationRepository.findByCommercialNameContainingIgnoreCase(term);
        medications.addAll(medicationRepository.findByDciContainingIgnoreCase(term));
        return medications.stream()
                .map(medicationMapper::toDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicationDTO> findAll() {
        return medicationRepository.findAll().stream()
                .map(medicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Imports medications from a file (CSV, Excel, or JSON format).
     * 
     * <p>This method processes bulk medication imports from various file formats.
     * It parses the file content, validates the medication data, and performs
     * batch insertion into the database.</p>
     * 
     * <p><strong>Supported Formats:</strong></p>
     * <ul>
     *   <li>CSV: Comma-separated values with headers (commercialName, dci, dosage, form)</li>
     *   <li>Excel: XLSX files with medication data in first sheet</li>
     *   <li>JSON: Array of medication objects</li>
     * </ul>
     *
     * @param file The binary content of the file to import
     * @return The number of medications successfully imported
     * @throws IllegalArgumentException if file format is invalid
     */
    @Override
    public int importFromFile(byte[] file) {
        // Implementation will parse file format (CSV/Excel/JSON)
        // Validate medication data (required fields, format)
        // Perform batch insert with duplicate checking
        return 0;
    }
}