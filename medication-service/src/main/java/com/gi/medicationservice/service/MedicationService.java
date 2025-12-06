package com.gi.medicationservice.service;

import com.gi.medicationservice.model.dto.MedicationDTO;

import java.util.List;

public interface MedicationService {

    MedicationDTO createMedication(MedicationDTO dto);

    MedicationDTO updateMedication(Long id, MedicationDTO dto);

    void deleteMedication(Long id);

    MedicationDTO findById(Long id);

    List<MedicationDTO> autocomplete(String term);

    List<MedicationDTO> findAll();

    int importFromFile(byte[] file);
}