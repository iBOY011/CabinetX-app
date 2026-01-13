package com.gi.billingservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gi.billingservice.client.ConsultationClient;
import com.gi.billingservice.exception.BusinessException;
import com.gi.billingservice.exception.ResourceNotFoundException;
import com.gi.billingservice.mapper.InvoiceMapper;
import com.gi.billingservice.model.dto.request.ConsultationDTO;
import com.gi.billingservice.model.dto.response.InvoiceDTO;
import com.gi.billingservice.model.entity.Invoice;
import com.gi.billingservice.model.enums.InvoiceStatus;
import com.gi.billingservice.model.enums.PaymentMethod;
import com.gi.billingservice.repository.InvoiceRepository;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private ConsultationClient consultationClient;

    @InjectMocks
    private BillingServiceImpl billingService;

    private ConsultationDTO consultationDTO;
    private Invoice invoice;
    private InvoiceDTO invoiceDTO;

    @BeforeEach
    void setUp() {
        consultationDTO = new ConsultationDTO();
        consultationDTO.setId(1L);
        consultationDTO.setPatientId(100L);
        consultationDTO.setMedecinId(200L);
        consultationDTO.setCabinetId(300L);

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV-12345678");
        invoice.setPatientId(100L);
        invoice.setConsultationId(1L);
        invoice.setCabinetId(300L);
        invoice.setDoctorId(200L);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setAmount(new BigDecimal("150.00"));
        invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);

        invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("INV-12345678");
        invoiceDTO.setPatientId(100L);
        invoiceDTO.setConsultationId(1L);
        invoiceDTO.setInvoiceDate(LocalDateTime.now());
        invoiceDTO.setAmount(new BigDecimal("150.00"));
        invoiceDTO.setStatus(InvoiceStatus.PENDING_PAYMENT);
        invoiceDTO.setPatientName("Mock Patient");
    }

    @Test
    void generateInvoice_ShouldCreateInvoice_WhenConsultationExists() {
        // Given
        Long consultationId = 1L;
        BigDecimal amount = new BigDecimal("150.00");

        when(consultationClient.getConsultation(consultationId)).thenReturn(consultationDTO);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toDTO(invoice, "Mock Patient")).thenReturn(invoiceDTO);

        // When
        InvoiceDTO result = billingService.generateInvoice(consultationId, amount);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPatientId()).isEqualTo(100L);
        assertThat(result.getConsultationId()).isEqualTo(consultationId);
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(result.getStatus()).isEqualTo(InvoiceStatus.PENDING_PAYMENT);

        verify(consultationClient).getConsultation(consultationId);
        verify(invoiceRepository).save(any(Invoice.class));
        verify(invoiceMapper).toDTO(invoice, "Mock Patient");
    }

    @Test
    void generateInvoice_ShouldThrowException_WhenConsultationNotFound() {
        // Given
        Long consultationId = 1L;
        BigDecimal amount = new BigDecimal("150.00");

        when(consultationClient.getConsultation(consultationId)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> billingService.generateInvoice(consultationId, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Consultation not found with id: " + consultationId);
    }

    @Test
    void recordPayment_ShouldUpdateInvoice_WhenInvoiceExistsAndPending() {
        // Given
        Long invoiceId = 1L;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
        Invoice paidInvoice = new Invoice();
        paidInvoice.setId(invoice.getId());
        paidInvoice.setInvoiceNumber(invoice.getInvoiceNumber());
        paidInvoice.setPatientId(invoice.getPatientId());
        paidInvoice.setConsultationId(invoice.getConsultationId());
        paidInvoice.setCabinetId(invoice.getCabinetId());
        paidInvoice.setDoctorId(invoice.getDoctorId());
        paidInvoice.setInvoiceDate(invoice.getInvoiceDate());
        paidInvoice.setAmount(invoice.getAmount());
        paidInvoice.setStatus(InvoiceStatus.PAID);
        paidInvoice.setPaymentMethod(paymentMethod);
        paidInvoice.setPaymentDate(LocalDateTime.now());

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(paidInvoice);
        when(invoiceMapper.toDTO(paidInvoice, "Mock Patient")).thenReturn(invoiceDTO);

        // When
        InvoiceDTO result = billingService.recordPayment(invoiceId, paymentMethod);

        // Then
        assertThat(result).isNotNull();
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceRepository).save(any(Invoice.class));
        verify(invoiceMapper).toDTO(paidInvoice, "Mock Patient");
    }

    @Test
    void recordPayment_ShouldThrowException_WhenInvoiceNotFound() {
        // Given
        Long invoiceId = 1L;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> billingService.recordPayment(invoiceId, paymentMethod))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    void recordPayment_ShouldThrowException_WhenInvoiceNotPending() {
        // Given
        Long invoiceId = 1L;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        invoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        // When & Then
        assertThatThrownBy(() -> billingService.recordPayment(invoiceId, paymentMethod))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invoice is not in pending payment status");
    }

    @Test
    void cancelInvoice_ShouldUpdateStatus_WhenInvoiceExists() {
        // Given
        Long invoiceId = 1L;

        Invoice cancelledInvoice = new Invoice();
        cancelledInvoice.setId(invoice.getId());
        cancelledInvoice.setInvoiceNumber(invoice.getInvoiceNumber());
        cancelledInvoice.setPatientId(invoice.getPatientId());
        cancelledInvoice.setConsultationId(invoice.getConsultationId());
        cancelledInvoice.setCabinetId(invoice.getCabinetId());
        cancelledInvoice.setDoctorId(invoice.getDoctorId());
        cancelledInvoice.setInvoiceDate(invoice.getInvoiceDate());
        cancelledInvoice.setAmount(invoice.getAmount());
        cancelledInvoice.setStatus(InvoiceStatus.CANCELLED);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(cancelledInvoice);
        when(invoiceMapper.toDTO(cancelledInvoice, "Mock Patient")).thenReturn(invoiceDTO);

        // When
        InvoiceDTO result = billingService.cancelInvoice(invoiceId);

        // Then
        assertThat(result).isNotNull();
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceRepository).save(any(Invoice.class));
        verify(invoiceMapper).toDTO(cancelledInvoice, "Mock Patient");
    }

    @Test
    void cancelInvoice_ShouldThrowException_WhenInvoiceNotFound() {
        // Given
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> billingService.cancelInvoice(invoiceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    void findById_ShouldReturnInvoice_WhenExists() {
        // Given
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toDTO(invoice, "Mock Patient")).thenReturn(invoiceDTO);

        // When
        InvoiceDTO result = billingService.findById(invoiceId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(invoiceId);
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceMapper).toDTO(invoice, "Mock Patient");
    }

    @Test
    void findById_ShouldThrowException_WhenInvoiceNotFound() {
        // Given
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> billingService.findById(invoiceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    void listInvoicesByPatient_ShouldReturnInvoiceList_WhenInvoicesExist() {
        // Given
        Long patientId = 100L;
        List<Invoice> invoices = Arrays.asList(invoice);

        when(invoiceRepository.findByPatientId(patientId)).thenReturn(invoices);
        when(invoiceMapper.toDTO(invoice, "Mock Patient")).thenReturn(invoiceDTO);

        // When
        List<InvoiceDTO> result = billingService.listInvoicesByPatient(patientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(patientId);
        verify(invoiceRepository).findByPatientId(patientId);
        verify(invoiceMapper).toDTO(invoice, "Mock Patient");
    }

    @Test
    void listInvoicesByPatient_ShouldReturnEmptyList_WhenNoInvoices() {
        // Given
        Long patientId = 100L;
        List<Invoice> invoices = Arrays.asList();

        when(invoiceRepository.findByPatientId(patientId)).thenReturn(invoices);

        // When
        List<InvoiceDTO> result = billingService.listInvoicesByPatient(patientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(invoiceRepository).findByPatientId(patientId);
    }

    @Test
    void generatePdf_ShouldReturnByteArray_WhenInvoiceExists() {
        // Given
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        // When
        byte[] result = billingService.generatePdf(invoiceId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        verify(invoiceRepository).findById(invoiceId);
    }

    @Test
    void generatePdf_ShouldThrowException_WhenInvoiceNotFound() {
        // Given
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> billingService.generatePdf(invoiceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }
}
