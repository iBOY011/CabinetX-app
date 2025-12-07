package com.gi.medicalrecordservice.service;

import com.gi.medicalrecordservice.client.ConsultationServiceClient;
import com.gi.medicalrecordservice.dto.ConsultationInfo;
import com.gi.medicalrecordservice.dto.DocumentMedicalDTO;
import com.gi.medicalrecordservice.dto.DossierMedicalDTO;
import com.gi.medicalrecordservice.entities.DocumentMedical;
import com.gi.medicalrecordservice.entities.DossierMedical;
import com.gi.medicalrecordservice.enums.TypeDocument;
import com.gi.medicalrecordservice.mappers.DossierMedicalMapper;
import com.gi.medicalrecordservice.repository.DocumentMedicalRepository;
import com.gi.medicalrecordservice.repository.DossierMedicalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DossierMedicalService {

    private final DossierMedicalRepository dossierRepository;
    private final DocumentMedicalRepository documentRepository;
    private final ConsultationServiceClient consultationServiceClient;
    private final DossierMedicalMapper dossierMedicalMapper;

    public DossierMedicalService(DossierMedicalRepository dossierRepository,
                                 DocumentMedicalRepository documentRepository,
                                 ConsultationServiceClient consultationServiceClient,
                                 DossierMedicalMapper dossierMedicalMapper) {
        this.dossierRepository = dossierRepository;
        this.documentRepository = documentRepository;
        this.consultationServiceClient = consultationServiceClient;
        this.dossierMedicalMapper = dossierMedicalMapper;
    }

    public DossierMedicalDTO ouvrirOuCreerDossier(Long patientId) {
        DossierMedical dossier = dossierRepository.findByPatientId(patientId)
            .orElseGet(() -> dossierRepository.save(DossierMedical.builder()
                .patientId(patientId)
                .build()));
        return enrichDossier(dossier, patientId);
    }

    public DossierMedicalDTO mettreAJourDossier(Long dossierId, DossierMedicalDTO dto) {
        DossierMedical dossier = chargerDossier(dossierId);
        dossier.setAntecedents(dto.getAntecedents());
        dossier.setAllergies(dto.getAllergies());
        dossier.setTraitements(dto.getTraitements());
        dossier.setHabitudes(dto.getHabitudes());
        DossierMedical saved = dossierRepository.save(dossier);
        return enrichDossier(saved, saved.getPatientId());
    }

    @Transactional(readOnly = true)
    public DossierMedicalDTO obtenirDossierComplet(Long patientId) {
        DossierMedical dossier = dossierRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dossier introuvable"));
        return enrichDossier(dossier, patientId);
    }

    public DocumentMedicalDTO ajouterDocument(Long dossierId, MultipartFile fichier, TypeDocument typeDocument) {
        DossierMedical dossier = chargerDossier(dossierId);
        try {
            DocumentMedical document = DocumentMedical.builder()
                    .dossier(dossier)
                    .nomFichier(fichier.getOriginalFilename())
                    .typeFichier(fichier.getContentType())
                    .typeDocument(typeDocument)
                    .contenu(fichier.getBytes())
                    .urlFichier(null)
                    .build();
            DocumentMedical saved = documentRepository.save(document);
            return dossierMedicalMapper.toDocumentDto(saved);
        } catch (IOException e) {
            log.error("Erreur lors de l'ajout du document", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de lire le fichier");
        }
    }

    public void supprimerDocument(Long documentId) {
        DocumentMedical document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document introuvable"));
        documentRepository.delete(document);
    }

    @Transactional(readOnly = true)
    public List<ConsultationInfo> obtenirHistoriqueConsultations(Long patientId) {
        try {
            return consultationServiceClient.getConsultationsByPatient(patientId);
        } catch (Exception ex) {
            log.warn("Impossible de récupérer l'historique des consultations pour le patient {}", patientId, ex);
            return Collections.emptyList();
        }
    }

    private DossierMedicalDTO enrichDossier(DossierMedical dossier, Long patientId) {
        List<DocumentMedical> documents = documentRepository.findByDossierId(dossier.getId());
        List<ConsultationInfo> consultations = obtenirHistoriqueConsultations(patientId);
        return dossierMedicalMapper.toDto(dossier, documents, consultations);
    }

    private DossierMedical chargerDossier(Long dossierId) {
        return dossierRepository.findById(dossierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dossier introuvable"));
    }
}
