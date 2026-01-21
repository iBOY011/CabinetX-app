package com.gi.patientservice.mappers;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.entities.Patient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre les entités Patient et leurs DTOs.
 * 
 * <p>Gère les transformations bidirectionnelles entre :
 * <ul>
 *   <li>PatientDTO (couche présentation/API) ↔ Patient (couche persistence)</li>
 * </ul>
 * 
 * <p>Préserve tous les champs incluant les adresses embarquées et les métadonnées
 * de création. Gère correctement les valeurs null pour éviter les NullPointerException.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Component
public class PatientMapper {

    /**
     * Convertit un PatientDTO en entité Patient.
     * 
     * @param dto le DTO à convertir
     * @return l'entité Patient correspondante, ou null si dto est null
     */
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

    /**
     * Convertit une entité Patient en PatientDTO.
     * 
     * @param entity l'entité à convertir
     * @return le DTO correspondant, ou null si entity est null
     */
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
