package com.gi.clinicservice.mapper;

import com.gi.clinicservice.model.dto.ClinicDTO;
import com.gi.clinicservice.model.entity.Clinic;
import com.gi.clinicservice.model.enums.ClinicStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClinicMapper Unit Tests")
class ClinicMapperTest {

    private ClinicMapper mapper;
    private Clinic clinic;
    private ClinicDTO clinicDTO;

    @BeforeEach
    void setUp() {
        mapper = new ClinicMapper();

        // Setup test entity
        clinic = new Clinic();
        clinic.setId(1L);
        clinic.setName("Cabinet Médical Test");
        clinic.setSpecialty("Médecine Générale");
        clinic.setPhone("0612345678");
        clinic.setAddress("123 Rue Test, Casablanca");
        clinic.setLogoUrl("https://example.com/logo.png");
        clinic.setStatus(ClinicStatus.ACTIVE);
        clinic.setServiceEndDate(LocalDate.of(2025, 12, 31));

        // Setup test DTO
        clinicDTO = new ClinicDTO();
        clinicDTO.setId(1L);
        clinicDTO.setName("Cabinet Médical Test");
        clinicDTO.setSpecialty("Médecine Générale");
        clinicDTO.setPhone("0612345678");
        clinicDTO.setAddress("123 Rue Test, Casablanca");
        clinicDTO.setLogoUrl("https://example.com/logo.png");
        clinicDTO.setStatus(ClinicStatus.ACTIVE);
        clinicDTO.setServiceEndDate(LocalDate.of(2025, 12, 31));
    }

    // ============ toEntity Tests ============

    @Test
    @DisplayName("Should map DTO to Entity successfully")
    void testToEntity_Success() {
        // When
        Clinic result = mapper.toEntity(clinicDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clinicDTO.getId());
        assertThat(result.getName()).isEqualTo(clinicDTO.getName());
        assertThat(result.getSpecialty()).isEqualTo(clinicDTO.getSpecialty());
        assertThat(result.getPhone()).isEqualTo(clinicDTO.getPhone());
        assertThat(result.getAddress()).isEqualTo(clinicDTO.getAddress());
        assertThat(result.getLogoUrl()).isEqualTo(clinicDTO.getLogoUrl());
        assertThat(result.getStatus()).isEqualTo(clinicDTO.getStatus());
        assertThat(result.getServiceEndDate()).isEqualTo(clinicDTO.getServiceEndDate());
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void testToEntity_NullDTO() {
        // When
        Clinic result = mapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map DTO with null optional fields to Entity")
    void testToEntity_NullOptionalFields() {
        // Given
        clinicDTO.setLogoUrl(null);

        // When
        Clinic result = mapper.toEntity(clinicDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLogoUrl()).isNull();
        assertThat(result.getName()).isEqualTo(clinicDTO.getName());
    }

    // ============ toResponse Tests ============

    @Test
    @DisplayName("Should map Entity to DTO successfully")
    void testToResponse_Success() {
        // When
        ClinicDTO result = mapper.toResponse(clinic);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clinic.getId());
        assertThat(result.getName()).isEqualTo(clinic.getName());
        assertThat(result.getSpecialty()).isEqualTo(clinic.getSpecialty());
        assertThat(result.getPhone()).isEqualTo(clinic.getPhone());
        assertThat(result.getAddress()).isEqualTo(clinic.getAddress());
        assertThat(result.getLogoUrl()).isEqualTo(clinic.getLogoUrl());
        assertThat(result.getStatus()).isEqualTo(clinic.getStatus());
        assertThat(result.getServiceEndDate()).isEqualTo(clinic.getServiceEndDate());
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void testToResponse_NullEntity() {
        // When
        ClinicDTO result = mapper.toResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map Entity with null optional fields to DTO")
    void testToResponse_NullOptionalFields() {
        // Given
        clinic.setLogoUrl(null);

        // When
        ClinicDTO result = mapper.toResponse(clinic);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLogoUrl()).isNull();
        assertThat(result.getName()).isEqualTo(clinic.getName());
    }

    // ============ updateEntity Tests ============

    @Test
    @DisplayName("Should update Entity from DTO successfully")
    void testUpdateEntity_Success() {
        // Given
        Clinic existingClinic = new Clinic();
        existingClinic.setId(999L); // Different ID to verify it's not changed
        existingClinic.setName("Old Name");
        existingClinic.setSpecialty("Old Specialty");
        existingClinic.setPhone("0600000000");
        existingClinic.setAddress("Old Address");
        existingClinic.setStatus(ClinicStatus.INACTIVE);

        // When
        mapper.updateEntity(clinicDTO, existingClinic);

        // Then
        // ID should NOT be updated
        assertThat(existingClinic.getId()).isEqualTo(999L);

        // All other fields should be updated
        assertThat(existingClinic.getName()).isEqualTo(clinicDTO.getName());
        assertThat(existingClinic.getSpecialty()).isEqualTo(clinicDTO.getSpecialty());
        assertThat(existingClinic.getPhone()).isEqualTo(clinicDTO.getPhone());
        assertThat(existingClinic.getAddress()).isEqualTo(clinicDTO.getAddress());
        assertThat(existingClinic.getLogoUrl()).isEqualTo(clinicDTO.getLogoUrl());
        assertThat(existingClinic.getStatus()).isEqualTo(clinicDTO.getStatus());
        assertThat(existingClinic.getServiceEndDate()).isEqualTo(clinicDTO.getServiceEndDate());
    }

    @Test
    @DisplayName("Should not modify Entity when DTO is null")
    void testUpdateEntity_NullDTO() {
        // Given
        Clinic existingClinic = new Clinic();
        existingClinic.setId(999L);
        existingClinic.setName("Original Name");

        // When
        mapper.updateEntity(null, existingClinic);

        // Then - Entity should remain unchanged
        assertThat(existingClinic.getId()).isEqualTo(999L);
        assertThat(existingClinic.getName()).isEqualTo("Original Name");
    }

    @Test
    @DisplayName("Should not throw exception when Entity is null")
    void testUpdateEntity_NullEntity() {
        // When & Then - Should not throw exception
        mapper.updateEntity(clinicDTO, null);
    }

    @Test
    @DisplayName("Should not throw exception when both DTO and Entity are null")
    void testUpdateEntity_BothNull() {
        // When & Then - Should not throw exception
        mapper.updateEntity(null, null);
    }

    @Test
    @DisplayName("Should preserve Entity ID when updating")
    void testUpdateEntity_PreservesId() {
        // Given
        Long originalId = 123L;
        Clinic existingClinic = new Clinic();
        existingClinic.setId(originalId);

        clinicDTO.setId(999L); // Different ID in DTO

        // When
        mapper.updateEntity(clinicDTO, existingClinic);

        // Then - ID should not be changed
        assertThat(existingClinic.getId()).isEqualTo(originalId);
    }

    // ============ Round-trip Tests ============

    @Test
    @DisplayName("Should maintain data integrity in round-trip DTO -> Entity -> DTO")
    void testRoundTrip_DtoToEntityToDto() {
        // When
        Clinic entity = mapper.toEntity(clinicDTO);
        ClinicDTO resultDTO = mapper.toResponse(entity);

        // Then
        assertThat(resultDTO).isNotNull();
        assertThat(resultDTO.getId()).isEqualTo(clinicDTO.getId());
        assertThat(resultDTO.getName()).isEqualTo(clinicDTO.getName());
        assertThat(resultDTO.getSpecialty()).isEqualTo(clinicDTO.getSpecialty());
        assertThat(resultDTO.getPhone()).isEqualTo(clinicDTO.getPhone());
        assertThat(resultDTO.getAddress()).isEqualTo(clinicDTO.getAddress());
        assertThat(resultDTO.getLogoUrl()).isEqualTo(clinicDTO.getLogoUrl());
        assertThat(resultDTO.getStatus()).isEqualTo(clinicDTO.getStatus());
        assertThat(resultDTO.getServiceEndDate()).isEqualTo(clinicDTO.getServiceEndDate());
    }

    @Test
    @DisplayName("Should maintain data integrity in round-trip Entity -> DTO -> Entity")
    void testRoundTrip_EntityToDtoToEntity() {
        // When
        ClinicDTO dto = mapper.toResponse(clinic);
        Clinic resultEntity = mapper.toEntity(dto);

        // Then
        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getId()).isEqualTo(clinic.getId());
        assertThat(resultEntity.getName()).isEqualTo(clinic.getName());
        assertThat(resultEntity.getSpecialty()).isEqualTo(clinic.getSpecialty());
        assertThat(resultEntity.getPhone()).isEqualTo(clinic.getPhone());
        assertThat(resultEntity.getAddress()).isEqualTo(clinic.getAddress());
        assertThat(resultEntity.getLogoUrl()).isEqualTo(clinic.getLogoUrl());
        assertThat(resultEntity.getStatus()).isEqualTo(clinic.getStatus());
        assertThat(resultEntity.getServiceEndDate()).isEqualTo(clinic.getServiceEndDate());
    }
}
