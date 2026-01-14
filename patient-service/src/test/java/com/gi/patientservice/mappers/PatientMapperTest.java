package com.gi.patientservice.mappers;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.entities.Adresse;
import com.gi.patientservice.entities.Patient;
import com.gi.patientservice.enums.Sexe;
import com.gi.patientservice.enums.TypeMutuelle;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private final PatientMapper mapper = new PatientMapper();

    @Test
    void toEntity_mapsAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        PatientDTO dto = PatientDTO.builder()
                .id(10L)
                .cin("AB123456")
                .nom("Doe")
                .prenom("John")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe(Sexe.MASCULIN)
                .numTel("0612345678")
                .typeMutuelle(TypeMutuelle.CNSS)
                .cabinetId(3L)
                .adresse(Adresse.builder().rue("1 rue").ville("Paris").codePostal("75000").pays("FR").build())
                .createdAt(createdAt)
                .build();

        Patient entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getCin()).isEqualTo("AB123456");
        assertThat(entity.getNom()).isEqualTo("Doe");
        assertThat(entity.getPrenom()).isEqualTo("John");
        assertThat(entity.getDateNaissance()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(entity.getSexe()).isEqualTo(Sexe.MASCULIN);
        assertThat(entity.getNumTel()).isEqualTo("0612345678");
        assertThat(entity.getTypeMutuelle()).isEqualTo(TypeMutuelle.CNSS);
        assertThat(entity.getCabinetId()).isEqualTo(3L);
        assertThat(entity.getAdresse().getVille()).isEqualTo("Paris");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void toDTO_mapsAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        Patient entity = Patient.builder()
                .id(5L)
                .cin("CD765432")
                .nom("Smith")
                .prenom("Jane")
                .dateNaissance(LocalDate.of(1985, 5, 20))
                .sexe(Sexe.FEMININ)
                .numTel("0712345678")
                .typeMutuelle(TypeMutuelle.PRIVEE)
                .cabinetId(8L)
                .adresse(Adresse.builder().rue("2 avenue").ville("Lyon").codePostal("69000").pays("FR").build())
                .createdAt(createdAt)
                .build();

        PatientDTO dto = mapper.toDTO(entity);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getCin()).isEqualTo("CD765432");
        assertThat(dto.getNom()).isEqualTo("Smith");
        assertThat(dto.getPrenom()).isEqualTo("Jane");
        assertThat(dto.getDateNaissance()).isEqualTo(LocalDate.of(1985, 5, 20));
        assertThat(dto.getSexe()).isEqualTo(Sexe.FEMININ);
        assertThat(dto.getNumTel()).isEqualTo("0712345678");
        assertThat(dto.getTypeMutuelle()).isEqualTo(TypeMutuelle.PRIVEE);
        assertThat(dto.getCabinetId()).isEqualTo(8L);
        assertThat(dto.getAdresse().getVille()).isEqualTo("Lyon");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void toDTOList_handlesNullOrEmpty() {
        assertThat(mapper.toDTOList(null)).isEmpty();
        assertThat(mapper.toDTOList(List.of())).isEmpty();
    }

    @Test
    void toDTOList_mapsElements() {
        Patient entity = Patient.builder().id(1L).cin("EF12345").build();

        List<PatientDTO> result = mapper.toDTOList(List.of(entity));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCin()).isEqualTo("EF12345");
    }

    @Test
    void toEntity_returnsNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toDTO_returnsNullWhenEntityIsNull() {
        assertThat(mapper.toDTO(null)).isNull();
    }

    @Test
    void toEntity_handlesNullAdresse() {
        PatientDTO dto = PatientDTO.builder()
                .id(1L)
                .cin("AB123456")
                .adresse(null)
                .build();

        Patient entity = mapper.toEntity(dto);

        assertThat(entity.getAdresse()).isNull();
    }

    @Test
    void toDTO_handlesNullAdresse() {
        Patient entity = Patient.builder()
                .id(1L)
                .cin("AB123456")
                .adresse(null)
                .build();

        PatientDTO dto = mapper.toDTO(entity);

        assertThat(dto.getAdresse()).isNull();
    }

    @Test
    void toDTOList_mapsMultipleElements() {
        Patient entity1 = Patient.builder().id(1L).cin("AA11111").nom("Nom1").build();
        Patient entity2 = Patient.builder().id(2L).cin("BB22222").nom("Nom2").build();
        Patient entity3 = Patient.builder().id(3L).cin("CC33333").nom("Nom3").build();

        List<PatientDTO> result = mapper.toDTOList(List.of(entity1, entity2, entity3));

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCin()).isEqualTo("AA11111");
        assertThat(result.get(1).getCin()).isEqualTo("BB22222");
        assertThat(result.get(2).getCin()).isEqualTo("CC33333");
    }
}
