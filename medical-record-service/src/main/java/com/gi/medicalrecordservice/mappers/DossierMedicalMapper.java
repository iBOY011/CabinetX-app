package com.gi.medicalrecordservice.mappers;

import com.gi.medicalrecordservice.dto.ConsultationInfo;
import com.gi.medicalrecordservice.dto.DocumentMedicalDTO;
import com.gi.medicalrecordservice.dto.DossierMedicalDTO;
import com.gi.medicalrecordservice.entities.DocumentMedical;
import com.gi.medicalrecordservice.entities.DossierMedical;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DossierMedicalMapper {

    public DossierMedicalDTO toDto(DossierMedical dossier,
                                   List<DocumentMedical> documents,
                                   List<ConsultationInfo> consultations) {
        return DossierMedicalDTO.builder()
                .id(dossier.getId())
                .patientId(dossier.getPatientId())
                .antecedents(dossier.getAntecedents())
                .allergies(dossier.getAllergies())
                .traitements(dossier.getTraitements())
                .habitudes(dossier.getHabitudes())
                .dateCreation(dossier.getDateCreation())
                .derniereMiseAJour(dossier.getDerniereMiseAJour())
                .documents(toDocumentDtoList(documents))
                .historiqueConsultations(consultations)
                .build();
    }

    public List<DocumentMedicalDTO> toDocumentDtoList(List<DocumentMedical> documents) {
        if (Objects.isNull(documents) || documents.isEmpty()) {
            return Collections.emptyList();
        }
        return documents.stream()
                .map(this::toDocumentDto)
                .collect(Collectors.toList());
    }

    public DocumentMedicalDTO toDocumentDto(DocumentMedical document) {
        if (document == null) {
            return null;
        }
        return DocumentMedicalDTO.builder()
                .id(document.getId())
                .nomFichier(document.getNomFichier())
                .typeFichier(document.getTypeFichier())
                .urlFichier(document.getUrlFichier())
                .typeDocument(document.getTypeDocument())
                .dateAjout(document.getDateAjout())
                .build();
    }
}
