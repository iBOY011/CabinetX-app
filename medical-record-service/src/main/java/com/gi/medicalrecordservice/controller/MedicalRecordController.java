package com.gi.medicalrecordservice.controller;

import com.gi.medicalrecordservice.model.dto.response.ConsultationInfo;
import com.gi.medicalrecordservice.model.dto.response.MedicalDocumentDTO;
import com.gi.medicalrecordservice.model.dto.response.MedicalRecordDTO;
import com.gi.medicalrecordservice.model.enums.DocumentType;
import com.gi.medicalrecordservice.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des dossiers médicaux patients.
 * 
 * <p>Expose les endpoints suivants :
 * <ul>
 *   <li>POST /api/records/patients/{patientId} : Création/ouverture dossier</li>
 *   <li>PUT /api/records/{recordId} : Mise à jour du dossier</li>
 *   <li>GET /api/records/patients/{patientId} : Consultation dossier complet</li>
 *   <li>POST /api/records/{recordId}/documents : Ajout document (upload)</li>
 *   <li>DELETE /api/records/documents/{documentId} : Suppression document</li>
 *   <li>GET /api/records/patients/{patientId}/consultations : Historique consultations</li>
 * </ul>
 * 
 * <p>Structure du dossier médical :
 * <ul>
 *   <li>Antécédents médicaux (medicalHistory)</li>
 *   <li>Allergies connues (allergies)</li>
 *   <li>Traitements en cours (treatments)</li>
 *   <li>Habitudes de vie (habits)</li>
 *   <li>Documents attachés (radiographies, analyses, ordonnances)</li>
 *   <li>Historique consultations (via ConsultationService)</li>
 * </ul>
 * 
 * <p>Gestion des documents :
 * <ul>
 *   <li>Upload multipart/form-data</li>
 *   <li>Types supportés : RADIOLOGY, LAB_RESULT, PRESCRIPTION, OTHER</li>
 *   <li>Stockage : Base64 en base de données (content BLOB)</li>
 *   <li>Limite : Configurable (défaut 10MB par fichier)</li>
 * </ul>
 * 
 * <p>Sécurité :
 * <ul>
 *   <li>MEDECIN : Accès complet (lecture/écriture)</li>
 *   <li>SECRETAIRE : Lecture uniquement</li>
 *   <li>PATIENT : Lecture de son propre dossier</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Ouvre ou crée le dossier médical d'un patient.
     * 
     * <p>Comportement idempotent :
     * <ul>
     *   <li>Si dossier existe : le retourne</li>
     *   <li>Si dossier n'existe pas : le crée et le retourne</li>
     * </ul>
     * 
     * <p>Appelé automatiquement lors de la première consultation d'un patient.
     * 
     * @param patientId identifiant du patient
     * @return MedicalRecordDTO complet avec historique consultations
     */
    @PostMapping("/patients/{patientId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordDTO openOrCreateRecord(@PathVariable Long patientId) {
        return medicalRecordService.openOrCreateRecord(patientId);
    }

    @PutMapping("/{recordId}")
    public MedicalRecordDTO updateRecord(@PathVariable Long recordId,
                                         @RequestBody @Valid MedicalRecordDTO dto) {
        return medicalRecordService.updateRecord(recordId, dto);
    }

    @GetMapping("/patients/{patientId}")
    public MedicalRecordDTO getRecord(@PathVariable Long patientId) {
        return medicalRecordService.getCompleteRecord(patientId);
    }

    /**
     * Ajoute un document médical au dossier (radiographie, analyse, etc.).
     * 
     * <p>Upload multipart/form-data avec deux paramètres :
     * <ul>
     *   <li>file : Le fichier à uploader (PDF, JPG, PNG, DICOM)</li>
     *   <li>type : Type de document (RADIOLOGY, LAB_RESULT, PRESCRIPTION, OTHER)</li>
     * </ul>
     * 
     * <p>Exemple curl :
     * <pre>
     * curl -X POST http://localhost:8083/api/records/1/documents \
     *   -F "file=@radio-poumon.jpg" \
     *   -F "type=RADIOLOGY"
     * </pre>
     * 
     * @param recordId identifiant du dossier médical
     * @param file fichier à uploader
     * @param documentType type de document
     * @return MedicalDocumentDTO du document créé
     * @throws ResponseStatusException si fichier illisible ou trop volumineux
     */
    @PostMapping(value = "/{recordId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalDocumentDTO addDocument(@PathVariable Long recordId,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("type") DocumentType documentType) {
        return medicalRecordService.addDocument(recordId, file, documentType);
    }

    @DeleteMapping("/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable Long documentId) {
        medicalRecordService.deleteDocument(documentId);
    }

    @GetMapping("/patients/{patientId}/consultations")
    public List<ConsultationInfo> consultationHistory(@PathVariable Long patientId) {
        return medicalRecordService.getConsultationHistory(patientId);
    }
}