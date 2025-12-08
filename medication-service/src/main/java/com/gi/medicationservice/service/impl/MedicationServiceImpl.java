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

    @Override
    public int importFromFile(byte[] file) {
        // TODO: Implement file import logic
        // For now, return 0
        return 0;
    }
}