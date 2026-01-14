package com.gi.patientservice.entities;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;

class PatientTest {

    @Test
    void builder_createsPatientWithAllFields() {
        Adresse adresse = Adresse.builder()
                .rue("10 Avenue Mohammed V")
                .ville("Casablanca")
                .codePostal("20000")
                .pays("Maroc")
                .build();

        Patient patient = Patient.builder()
                .id(1L)
                .cin("AB123456")
                .nom("Alami")
                .prenom("Youssef")
                .dateNaissance(LocalDate.of(1985, 3, 15))
                .sexe(Sexe.MASCULIN)
                .numTel("0612345678")
                .typeMutuelle(TypeMutuelle.CNOPS)
                .cabinetId(10L)
                .adresse(adresse)
                .build();

        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getCin()).isEqualTo("AB123456");
        assertThat(patient.getNom()).isEqualTo("Alami");
        assertThat(patient.getPrenom()).isEqualTo("Youssef");
        assertThat(patient.getDateNaissance()).isEqualTo(LocalDate.of(1985, 3, 15));
        assertThat(patient.getSexe()).isEqualTo(Sexe.MASCULIN);
        assertThat(patient.getNumTel()).isEqualTo("0612345678");
        assertThat(patient.getTypeMutuelle()).isEqualTo(TypeMutuelle.CNOPS);
        assertThat(patient.getCabinetId()).isEqualTo(10L);
        assertThat(patient.getAdresse()).isNotNull();
        assertThat(patient.getAdresse().getVille()).isEqualTo("Casablanca");
    }

    @Test
    void setters_modifyFields() {
        Patient patient = new Patient();
        patient.setId(2L);
        patient.setCin("CD987654");
        patient.setNom("Bennani");
        patient.setPrenom("Fatima");
        patient.setDateNaissance(LocalDate.of(1990, 7, 20));
        patient.setSexe(Sexe.FEMININ);
        patient.setNumTel("0712345678");
        patient.setTypeMutuelle(TypeMutuelle.PRIVEE);
        patient.setCabinetId(5L);

        assertThat(patient.getId()).isEqualTo(2L);
        assertThat(patient.getCin()).isEqualTo("CD987654");
        assertThat(patient.getNom()).isEqualTo("Bennani");
        assertThat(patient.getPrenom()).isEqualTo("Fatima");
        assertThat(patient.getSexe()).isEqualTo(Sexe.FEMININ);
        assertThat(patient.getTypeMutuelle()).isEqualTo(TypeMutuelle.PRIVEE);
    }

    @Test
    void noArgsConstructor_createsEmptyPatient() {
        Patient patient = new Patient();
        
        assertThat(patient.getId()).isNull();
        assertThat(patient.getCin()).isNull();
        assertThat(patient.getNom()).isNull();
    }

    @Test
    void allArgsConstructor_createsPatient() {
        Adresse adresse = new Adresse("Rue", "Ville", "12345", "Pays");
        Patient patient = new Patient(
                1L, "XY111111", "Nom", "Prenom",
                LocalDate.of(2000, 1, 1), Sexe.AUTRE, "0600000000",
                TypeMutuelle.AUCUNE, adresse, 1L, null, null
        );

        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getSexe()).isEqualTo(Sexe.AUTRE);
        assertThat(patient.getTypeMutuelle()).isEqualTo(TypeMutuelle.AUCUNE);
    }
}
