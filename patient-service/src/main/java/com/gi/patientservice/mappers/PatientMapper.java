package com.gi.patientservice.mappers;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.entities.Patient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PatientMapper {

    public Patient toEntity(PatientDTO dto) {
        if (dto == null) {
            return null;
        }
        return Patient.builder()
                .id(dto.getId())
                .cin(dto.getCin())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .numTel(dto.getNumTel())
                .typeMutuelle(dto.getTypeMutuelle())
                .cabinetId(dto.getCabinetId())
                .adresse(dto.getAdresse())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public PatientDTO toDTO(Patient entity) {
        if (entity == null) {
            return null;
        }
        return PatientDTO.builder()
                .id(entity.getId())
                .cin(entity.getCin())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .dateNaissance(entity.getDateNaissance())
                .sexe(entity.getSexe())
                .numTel(entity.getNumTel())
                .typeMutuelle(entity.getTypeMutuelle())
                .cabinetId(entity.getCabinetId())
                .adresse(entity.getAdresse())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<PatientDTO> toDTOList(List<Patient> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
