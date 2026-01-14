package com.gi.patientservice.dto;

import com.gi.patientservice.entities.Adresse;
import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PatientDTOTest {

    @Test
    void builder_createsPatientDTOWithAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        Adresse adresse = Adresse.builder()
                .rue("10 Rue Mohammed V")
                .ville("Marrakech")
                .codePostal("40000")
                .pays("Maroc")
                .build();

        PatientDTO dto = PatientDTO.builder()
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
                .createdAt(createdAt)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCin()).isEqualTo("AB123456");
        assertThat(dto.getNom()).isEqualTo("Alami");
        assertThat(dto.getPrenom()).isEqualTo("Youssef");
        assertThat(dto.getDateNaissance()).isEqualTo(LocalDate.of(1985, 3, 15));
        assertThat(dto.getSexe()).isEqualTo(Sexe.MASCULIN);
        assertThat(dto.getNumTel()).isEqualTo("0612345678");
        assertThat(dto.getTypeMutuelle()).isEqualTo(TypeMutuelle.CNOPS);
        assertThat(dto.getCabinetId()).isEqualTo(10L);
        assertThat(dto.getAdresse()).isNotNull();
        assertThat(dto.getAdresse().getVille()).isEqualTo("Marrakech");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void setters_modifyFields() {
        PatientDTO dto = new PatientDTO();
        dto.setId(2L);
        dto.setCin("CD987654");
        dto.setNom("Bennani");
        dto.setPrenom("Fatima");
        dto.setDateNaissance(LocalDate.of(1990, 7, 20));
        dto.setSexe(Sexe.FEMININ);
        dto.setNumTel("0712345678");
        dto.setTypeMutuelle(TypeMutuelle.PRIVEE);
        dto.setCabinetId(5L);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getCin()).isEqualTo("CD987654");
        assertThat(dto.getNom()).isEqualTo("Bennani");
        assertThat(dto.getPrenom()).isEqualTo("Fatima");
        assertThat(dto.getSexe()).isEqualTo(Sexe.FEMININ);
        assertThat(dto.getTypeMutuelle()).isEqualTo(TypeMutuelle.PRIVEE);
    }

    @Test
    void noArgsConstructor_createsEmptyDTO() {
        PatientDTO dto = new PatientDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getCin()).isNull();
        assertThat(dto.getNom()).isNull();
        assertThat(dto.getAdresse()).isNull();
    }

    @Test
    void allArgsConstructor_createsDTO() {
        Adresse adresse = new Adresse("Rue", "Ville", "12345", "Pays");
        LocalDateTime createdAt = LocalDateTime.now();
        
        PatientDTO dto = new PatientDTO(
                1L, "XY111111", "Nom", "Prenom",
                LocalDate.of(2000, 1, 1), Sexe.AUTRE, "0600000000",
                TypeMutuelle.AUCUNE, 1L, adresse, createdAt
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSexe()).isEqualTo(Sexe.AUTRE);
        assertThat(dto.getTypeMutuelle()).isEqualTo(TypeMutuelle.AUCUNE);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void equals_andHashCode_workCorrectly() {
        PatientDTO dto1 = PatientDTO.builder()
                .id(1L)
                .cin("AB123456")
                .nom("Test")
                .build();

        PatientDTO dto2 = PatientDTO.builder()
                .id(1L)
                .cin("AB123456")
                .nom("Test")
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void toString_containsRelevantFields() {
        PatientDTO dto = PatientDTO.builder()
                .id(1L)
                .cin("AB123456")
                .nom("TestNom")
                .prenom("TestPrenom")
                .build();

        String str = dto.toString();

        assertThat(str).contains("AB123456");
        assertThat(str).contains("TestNom");
        assertThat(str).contains("TestPrenom");
    }
}
