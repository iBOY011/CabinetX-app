package com.gi.medicalrecordservice.web;

import com.gi.medicalrecordservice.dto.ConsultationInfo;
import com.gi.medicalrecordservice.dto.DocumentMedicalDTO;
import com.gi.medicalrecordservice.dto.DossierMedicalDTO;
import com.gi.medicalrecordservice.enums.TypeDocument;
import com.gi.medicalrecordservice.service.DossierMedicalService;
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

@RestController
@RequestMapping("/api/dossiers")
@RequiredArgsConstructor
public class DossierMedicalController {

    private final DossierMedicalService dossierMedicalService;

    @PostMapping("/patients/{patientId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DossierMedicalDTO ouvrirOuCreerDossier(@PathVariable Long patientId) {
        return dossierMedicalService.ouvrirOuCreerDossier(patientId);
    }

    @PutMapping("/{dossierId}")
    public DossierMedicalDTO mettreAJourDossier(@PathVariable Long dossierId,
                                                @RequestBody @Valid DossierMedicalDTO dto) {
        return dossierMedicalService.mettreAJourDossier(dossierId, dto);
    }

    @GetMapping("/patients/{patientId}")
    public DossierMedicalDTO obtenirDossier(@PathVariable Long patientId) {
        return dossierMedicalService.obtenirDossierComplet(patientId);
    }

    @PostMapping(value = "/{dossierId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentMedicalDTO ajouterDocument(@PathVariable Long dossierId,
                                              @RequestParam("fichier") MultipartFile fichier,
                                              @RequestParam("type") TypeDocument typeDocument) {
        return dossierMedicalService.ajouterDocument(dossierId, fichier, typeDocument);
    }

    @DeleteMapping("/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerDocument(@PathVariable Long documentId) {
        dossierMedicalService.supprimerDocument(documentId);
    }

    @GetMapping("/patients/{patientId}/consultations")
    public List<ConsultationInfo> historiqueConsultations(@PathVariable Long patientId) {
        return dossierMedicalService.obtenirHistoriqueConsultations(patientId);
    }
}
