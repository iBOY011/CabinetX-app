package com.gi.patientservice;

import com.gi.patientservice.entities.Patient;
import com.gi.patientservice.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class PatientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(PatientRepository patientRepository) {
        return args -> {
            patientRepository.save(Patient.builder()
                    .cin("AB123456")
                    .nom("Alami")
                    .prenom("Mohammed")
                    .dateNaissance(LocalDate.of(1985, 3, 15))
                    .numTel("0612345678")
                    .cabinetId(1L)
                    .build());

            patientRepository.save(Patient.builder()
                    .cin("CD789012")
                    .nom("Bennani")
                    .prenom("Fatima")
                    .dateNaissance(LocalDate.of(1990, 7, 22))
                    .numTel("0623456789")
                    .cabinetId(1L)
                    .build());

            patientRepository.save(Patient.builder()
                    .cin("EF345678")
                    .nom("Idrissi")
                    .prenom("Karim")
                    .dateNaissance(LocalDate.of(1978, 11, 8))
                    .numTel("0634567890")
                    .cabinetId(1L)
                    .build());
        };
    }
}
