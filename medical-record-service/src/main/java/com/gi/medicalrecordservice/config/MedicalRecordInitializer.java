package com.gi.medicalrecordservice.config;

import com.gi.medicalrecordservice.model.entity.MedicalDocument;
import com.gi.medicalrecordservice.model.entity.MedicalRecord;
import com.gi.medicalrecordservice.model.enums.DocumentType;
import com.gi.medicalrecordservice.repository.MedicalRecordRepository;
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

    private final MedicalRecordRepository medicalRecordRepository;

    @PostConstruct
    @Transactional
    public void seedData() {
        if (medicalRecordRepository.count() > 0) {
            return;
        }

        for (long patientId = 1; patientId <= 3; patientId++) {
            MedicalRecord medicalRecord = MedicalRecord.builder()
                    .patientId(patientId)
                    .medicalHistory("Patient " + patientId + " with no major history.")
                    .build();

            medicalRecord.addDocument(MedicalDocument.builder()
                    .fileName("analysis-" + patientId + ".pdf")
                    .documentType(DocumentType.ANALYSIS)
                    .fileUrl("https://docs.example.com/" + UUID.randomUUID())
                    .content(new byte[0])
                    .additionDate(LocalDate.now().minusDays(patientId).atStartOfDay())
                    .build());

            medicalRecordRepository.save(medicalRecord);
        }

        log.info("Demo data inserted for medical records");
    }
}
