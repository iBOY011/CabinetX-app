package com.gi.prescriptionservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gi.prescriptionservice.mapper.PrescriptionMapper;
import com.gi.prescriptionservice.model.dto.PrescriptionDTO;
import com.gi.prescriptionservice.model.dto.PrescriptionLineDTO;
import com.gi.prescriptionservice.model.entity.Prescription;
import com.gi.prescriptionservice.model.entity.PrescriptionLine;
import com.gi.prescriptionservice.model.enums.PrescriptionLineType;
import com.gi.prescriptionservice.repository.PrescriptionLineRepository;
import com.gi.prescriptionservice.repository.PrescriptionRepository;
import com.gi.prescriptionservice.service.impl.PrescriptionServiceImpl;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionLineRepository prescriptionLineRepository;

    @Mock
    private PrescriptionMapper mapper;

    @InjectMocks
    private PrescriptionServiceImpl service;

    @Test
    void createPrescription_persistsSignatureAndLines() {
        var signature = "data:image/png;base64,abc";
        var lineDto = new PrescriptionLineDTO(null, null, PrescriptionLineType.MEDICATION, 3L, "doligripo", "7", 5, null);
        var dto = new PrescriptionDTO(null, 35L, 36L, 19L, 4L, LocalDate.now(), signature, List.of(lineDto));

        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        when(mapper.toEntity(any(PrescriptionLineDTO.class), any(Prescription.class))).thenAnswer(invocation -> {
            PrescriptionLine l = new PrescriptionLine();
            l.setPrescription(invocation.getArgument(1));
            l.setPrescriptionId(((Prescription) invocation.getArgument(1)).getId());
            l.setLineType(((PrescriptionLineDTO) invocation.getArgument(0)).getLineType());
            l.setMedicationId(((PrescriptionLineDTO) invocation.getArgument(0)).getMedicationId());
            l.setMedicationName(((PrescriptionLineDTO) invocation.getArgument(0)).getMedicationName());
            l.setDosage(((PrescriptionLineDTO) invocation.getArgument(0)).getDosage());
            l.setDurationDays(((PrescriptionLineDTO) invocation.getArgument(0)).getDurationDays());
            l.setComment(((PrescriptionLineDTO) invocation.getArgument(0)).getComment());
            return l;
        });

        when(mapper.toDTO(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription p = invocation.getArgument(0);
            return new PrescriptionDTO(p.getId(), p.getConsultationId(), p.getPatientId(), p.getDoctorId(), p.getClinicId(), p.getPrescriptionDate(), p.getDigitalSignature(), List.of());
        });

        PrescriptionDTO result = service.createPrescription(dto);

        ArgumentCaptor<Prescription> prescriptionCaptor = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository, times(1)).save(prescriptionCaptor.capture());
        Prescription saved = prescriptionCaptor.getValue();
        assertThat(saved.getDigitalSignature()).isEqualTo(signature);
        assertThat(saved.getClinicId()).isEqualTo(4L);

        verify(prescriptionLineRepository, times(1)).saveAll(any());
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getDigitalSignature()).isEqualTo(signature);
    }

    @Test
    void addLine_addsLineAndReturnsUpdatedDto() {
        Prescription existing = new Prescription();
        existing.setId(200L);
        existing.setLines(new ArrayList<>());

        var lineDto = new PrescriptionLineDTO(null, null, PrescriptionLineType.MEDICATION, 3L, "doligripo", "7", 5, "note");

        when(prescriptionRepository.findById(200L)).thenReturn(java.util.Optional.of(existing));

        when(mapper.toEntity(any(PrescriptionLineDTO.class), any(Prescription.class))).thenAnswer(invocation -> {
            PrescriptionLine l = new PrescriptionLine();
            l.setId(300L);
            l.setPrescription(existing);
            l.setPrescriptionId(existing.getId());
            l.setLineType(lineDto.getLineType());
            l.setMedicationId(lineDto.getMedicationId());
            l.setMedicationName(lineDto.getMedicationName());
            l.setDosage(lineDto.getDosage());
            l.setDurationDays(lineDto.getDurationDays());
            l.setComment(lineDto.getComment());
            return l;
        });

        when(mapper.toDTO(any(Prescription.class))).thenReturn(new PrescriptionDTO(existing.getId(), null, null, null, null, null, null, List.of()));

        PrescriptionDTO result = service.addLine(200L, lineDto);

        verify(prescriptionLineRepository, times(1)).save(any(PrescriptionLine.class));
        assertThat(existing.getLines()).hasSize(1);
        assertThat(result.getId()).isEqualTo(200L);
    }

    @Test
    void findByConsultationId_returnsDto() {
        Prescription prescription = new Prescription();
        prescription.setId(10L);
        prescription.setConsultationId(35L);

        when(prescriptionRepository.findByConsultationId(35L)).thenReturn(java.util.Optional.of(prescription));
        when(mapper.toDTO(prescription)).thenReturn(new PrescriptionDTO(10L, 35L, null, null, null, null, null, List.of()));

        PrescriptionDTO dto = service.findByConsultationId(35L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getConsultationId()).isEqualTo(35L);
    }
}
