package com.gi.medicationservice.mapper;

import com.gi.medicationservice.model.dto.MedicationDTO;
import com.gi.medicationservice.model.entity.Medication;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Medication (entité JPA) et MedicationDTO.
 * 
 * <p>Transforme les données entre couche persistance (PostgreSQL) 
 * et couche présentation (API REST).</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Component
public class MedicationMapper {

    /**
     * Convertit une entité Medication en MedicationDTO.
     * 
     * @param medication L'entité source (peut être null)
     * @return Le DTO ou null si medication est null
     * 
     * <p><b>Usage :</b> Réponses API (GET /api/medications/{id})</p>
     */
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