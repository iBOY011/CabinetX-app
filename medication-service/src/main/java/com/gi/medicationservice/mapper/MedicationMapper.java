package com.gi.medicationservice.mapper;

import com.gi.medicationservice.model.dto.MedicationDTO;
import com.gi.medicationservice.model.entity.Medication;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapper {

    public MedicationDTO toDTO(Medication medication) {
        if (medication == null) {
            return null;
        }
        MedicationDTO dto = new MedicationDTO();
        dto.setId(medication.getId());
        dto.setCommercialName(medication.getCommercialName());
        dto.setDci(medication.getDci());
        dto.setDosage(medication.getDosage());
        dto.setForm(medication.getForm());
        return dto;
    }

    public Medication toEntity(MedicationDTO dto) {
        if (dto == null) {
            return null;
        }
        Medication medication = new Medication();
        medication.setId(dto.getId());
        medication.setCommercialName(dto.getCommercialName());
        medication.setDci(dto.getDci());
        medication.setDosage(dto.getDosage());
        medication.setForm(dto.getForm());
        return medication;
    }
}