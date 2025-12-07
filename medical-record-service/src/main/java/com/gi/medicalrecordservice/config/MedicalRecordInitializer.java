package com.gi.medicalrecordservice.config;

import com.gi.medicalrecordservice.entities.DocumentMedical;
import com.gi.medicalrecordservice.entities.DossierMedical;
import com.gi.medicalrecordservice.enums.TypeDocument;
import com.gi.medicalrecordservice.repository.DossierMedicalRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordInitializer {

    private final DossierMedicalRepository dossierMedicalRepository;

    @PostConstruct
    @Transactional
    public void seedData() {
        if (dossierMedicalRepository.count() > 0) {
            return;
        }

        for (long patientId = 1; patientId <= 3; patientId++) {
                DossierMedical dossier = DossierMedical.builder()
                    .patientId(patientId)
                    .medecinReferent("Docteur Demo " + patientId)
                    .resumeMedical("Patient " + patientId + " sans antecedent majeur.")
                    .build();

                            dossier.ajouterDocument(DocumentMedical.builder()
                                .nomFichier("analyse-" + patientId + ".pdf")
                                .typeDocument(TypeDocument.ANALYSE)
                                .urlFichier("https://docs.example.com/" + UUID.randomUUID())
                                .contenu(new byte[0])
                                .dateAjout(LocalDate.now().minusDays(patientId).atStartOfDay())
                                .build());

            dossierMedicalRepository.save(dossier);
        }

        log.info("Données de démonstration insérées pour les dossiers médicaux");
    }
}
