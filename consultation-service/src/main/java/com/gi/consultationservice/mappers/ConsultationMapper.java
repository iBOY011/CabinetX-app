package com.gi.consultationservice.mappers;

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.entities.Consultation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ConsultationMapper {

    public Consultation toEntity(ConsultationDTO dto) {
        if (dto == null) {
            return null;
        }
        return Consultation.builder()
                .id(dto.getId())
                .rendezVousId(dto.getRendezVousId())
                .patientId(dto.getPatientId())
                .medecinId(dto.getMedecinId())
                .cabinetId(dto.getCabinetId())
                .type(dto.getType())
                .dateConsultation(dto.getDateConsultation())
                .examenClinique(dto.getExamenClinique())
                .examenSupplementaire(dto.getExamenSupplementaire())
                .diagnostic(dto.getDiagnostic())
                .traitement(dto.getTraitement())
                .observations(dto.getObservations())
                .build();
    }

    public ConsultationDTO toDTO(Consultation entity) {
        if (entity == null) {
            return null;
        }
        return ConsultationDTO.builder()
                .id(entity.getId())
                .rendezVousId(entity.getRendezVousId())
                .patientId(entity.getPatientId())
                .medecinId(entity.getMedecinId())
                .cabinetId(entity.getCabinetId())
                .type(entity.getType())
                .dateConsultation(entity.getDateConsultation())
                .examenClinique(entity.getExamenClinique())
                .examenSupplementaire(entity.getExamenSupplementaire())
                .diagnostic(entity.getDiagnostic())
                .traitement(entity.getTraitement())
                .observations(entity.getObservations())
                .build();
    }

    public ConsultationSummaryDTO toSummary(Consultation entity) {
        if (entity == null) {
            return null;
        }
        return ConsultationSummaryDTO.builder()
                .id(entity.getId())
                .dateConsultation(entity.getDateConsultation())
                .type(entity.getType())
                .diagnostic(entity.getDiagnostic())
                .build();
    }

    public List<ConsultationDTO> toDTOList(List<Consultation> consultations) {
        if (Objects.isNull(consultations) || consultations.isEmpty()) {
            return Collections.emptyList();
        }
        return consultations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ConsultationSummaryDTO> toSummaryList(List<Consultation> consultations) {
        if (Objects.isNull(consultations) || consultations.isEmpty()) {
            return Collections.emptyList();
        }
        return consultations.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
}
