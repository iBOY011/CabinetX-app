package com.gi.prescriptionservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;
import com.gi.prescriptionservice.model.entity.Prescription;
import com.gi.prescriptionservice.model.entity.PrescriptionLine;
import com.gi.prescriptionservice.model.enums.PrescriptionLineType;

class PrescriptionMapperTest {

    private final PrescriptionMapper mapper = new PrescriptionMapper();

    @Test
    void toDTO_mapsAllFieldsIncludingSignature() {
        Prescription entity = new Prescription();
        entity.setId(1L);
        entity.setConsultationId(10L);
        entity.setPatientId(20L);
        entity.setDoctorId(30L);
        entity.setClinicId(40L);
        entity.setPrescriptionDate(LocalDate.now());
        entity.setDigitalSignature("data:image/png;base64,abc");

        PrescriptionLine line = new PrescriptionLine();
        line.setId(2L);
        line.setPrescriptionId(1L);
        line.setLineType(PrescriptionLineType.MEDICATION);
        line.setMedicationId(3L);
        line.setMedicationName("doligripo");
        line.setDosage("7");
        line.setDurationDays(5);
        entity.setLines(List.of(line));

        PrescriptionDTO dto = mapper.toDTO(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDigitalSignature()).isEqualTo("data:image/png;base64,abc");
        assertThat(dto.getLines()).hasSize(1);
        assertThat(dto.getLines().get(0).getMedicationName()).isEqualTo("doligripo");
    }

    @Test
    void toEntity_mapsAllFieldsIncludingSignature() {
        PrescriptionLineDTO lineDto = new PrescriptionLineDTO(5L, 1L, PrescriptionLineType.IMAGING_EXAM, null, null, "", 3, "note");
        PrescriptionDTO dto = new PrescriptionDTO(1L, 10L, 20L, 30L, 40L, LocalDate.now(), "sig", List.of(lineDto));

        Prescription entity = mapper.toEntity(dto);

        assertThat(entity.getDigitalSignature()).isEqualTo("sig");
        assertThat(entity.getClinicId()).isEqualTo(40L);
        assertThat(entity.getLines()).hasSize(1);
        assertThat(entity.getLines().get(0).getLineType()).isEqualTo(PrescriptionLineType.IMAGING_EXAM);
    }
}
