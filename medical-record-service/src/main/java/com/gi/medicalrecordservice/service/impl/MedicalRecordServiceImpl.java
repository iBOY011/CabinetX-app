package com.gi.medicalrecordservice.service.impl;

import com.gi.medicalrecordservice.client.ConsultationServiceClient;
import com.gi.medicalrecordservice.mapper.MedicalRecordMapper;
import com.gi.medicalrecordservice.model.dto.response.ConsultationInfo;
import com.gi.medicalrecordservice.model.dto.response.MedicalDocumentDTO;
import com.gi.medicalrecordservice.model.dto.response.MedicalRecordDTO;
import com.gi.medicalrecordservice.model.entity.MedicalDocument;
import com.gi.medicalrecordservice.model.entity.MedicalRecord;
import com.gi.medicalrecordservice.model.enums.DocumentType;
import com.gi.medicalrecordservice.repository.MedicalDocumentRepository;
import com.gi.medicalrecordservice.repository.MedicalRecordRepository;
import com.gi.medicalrecordservice.service.MedicalRecordService;
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

/**
 * Implémentation du service de gestion des dossiers médicaux patients.
 * 
 * <p>Gère le cycle de vie complet des dossiers médicaux :
 * <ul>
 *   <li>Création automatique lors de la première consultation</li>
 *   <li>Mise à jour des antécédents, allergies, traitements</li>
 *   <li>Ajout/suppression de documents médicaux (BLOB storage)</li>
 *   <li>Enrichissement avec historique consultations (via ConsultationService)</li>
 * </ul>
 * 
 * <p>Architecture de stockage :
 * <ul>
 *   <li>MedicalRecord : Données textuelles (antécédents, allergies, etc.)</li>
 *   <li>MedicalDocument : Fichiers binaires (radiographies, analyses) en BLOB</li>
 *   <li>ConsultationHistory : Récupéré à la demande depuis ConsultationService</li>
 * </ul>
 * 
 * <p>Intégrations microservices :
 * <ul>
 *   <li>ConsultationServiceClient : Récupération historique consultations</li>
 *   <li>Future : PatientService pour validation patientId</li>
 * </ul>
 * 
 * <p>Pattern "Enrichment" :
 * Les méthodes enrichRecord() fusionnent les données locales (dossier)
 * avec les données distantes (consultations) pour fournir une vue complète.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Service
@Transactional
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalDocumentRepository medicalDocumentRepository;
    private final ConsultationServiceClient consultationServiceClient;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    MedicalDocumentRepository medicalDocumentRepository,
                                    ConsultationServiceClient consultationServiceClient,
                                    MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalDocumentRepository = medicalDocumentRepository;
        this.consultationServiceClient = consultationServiceClient;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    /**
     * Ouvre ou crée le dossier médical d'un patient.
     * 
     * <p>Pattern "Get or Create" : Vérifie l'existence du dossier,
     * le crée si absent, puis l'enrichit avec l'historique consultations.
     * 
     * @param patientId identifiant du patient
     * @return MedicalRecordDTO enrichi avec consultations
     */
    @Override
    public MedicalRecordDTO openOrCreateRecord(Long patientId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseGet(() -> medicalRecordRepository.save(MedicalRecord.builder()
                        .patientId(patientId)
                        .build()));
        return enrichRecord(medicalRecord, patientId);
    }

    @Override
    public MedicalRecordDTO updateRecord(Long recordId, MedicalRecordDTO dto) {
        MedicalRecord medicalRecord = loadRecord(recordId);
        medicalRecord.setMedicalHistory(dto.getMedicalHistory());
        medicalRecord.setAllergies(dto.getAllergies());
        medicalRecord.setTreatments(dto.getTreatments());
        medicalRecord.setHabits(dto.getHabits());
        MedicalRecord saved = medicalRecordRepository.save(medicalRecord);
        return enrichRecord(saved, saved.getPatientId());
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordDTO getCompleteRecord(Long patientId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));
        return enrichRecord(medicalRecord, patientId);
    }

    /**
     * Ajoute un document médical au dossier.
     * 
     * <p>Le fichier est converti en byte[] et stocké en base de données
     * dans le champ content (type BLOB). Les métadonnées (nom, type MIME,
     * type de document) sont également stockées.
     * 
     * <p>Formats supportés : Tous (PDF, JPG, PNG, DICOM, etc.)
     * Limite de taille : Définie par spring.servlet.multipart.max-file-size
     * 
     * @param recordId identifiant du dossier
     * @param file fichier multipart à uploader
     * @param documentType type de document médical
     * @return MedicalDocumentDTO du document créé
     * @throws ResponseStatusException si erreur lecture fichier
     */
    @Override
    public MedicalDocumentDTO addDocument(Long recordId, MultipartFile file, DocumentType documentType) {
        MedicalRecord medicalRecord = loadRecord(recordId);
        try {
            MedicalDocument document = MedicalDocument.builder()
                    .medicalRecord(medicalRecord)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .documentType(documentType)
                    .content(file.getBytes())
                    .fileUrl(null)
                    .build();
            MedicalDocument saved = medicalDocumentRepository.save(document);
            return medicalRecordMapper.toDocumentDto(saved);
        } catch (IOException e) {
            log.error("Error adding document", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read file");
        }
    }

    @Override
    public void deleteDocument(Long documentId) {
        MedicalDocument document = medicalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        medicalDocumentRepository.delete(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationInfo> getConsultationHistory(Long patientId) {
        try {
            return consultationServiceClient.getConsultationsByPatient(patientId);
        } catch (Exception ex) {
            log.warn("Unable to retrieve consultation history for patient {}", patientId, ex);
            return Collections.emptyList();
        }
    }

    private MedicalRecordDTO enrichRecord(MedicalRecord medicalRecord, Long patientId) {
        List<MedicalDocument> documents = medicalDocumentRepository.findByMedicalRecordId(medicalRecord.getId());
        List<ConsultationInfo> consultations = getConsultationHistory(patientId);
        return medicalRecordMapper.toDto(medicalRecord, documents, consultations);
    }

    private MedicalRecord loadRecord(Long recordId) {
        return medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));
    }
}