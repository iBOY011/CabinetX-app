package com.gi.billingservice.service.impl;

import com.gi.billingservice.exception.BusinessException;
import com.gi.billingservice.exception.ResourceNotFoundException;
import com.gi.billingservice.mapper.InvoiceMapper;
import com.gi.billingservice.model.dto.request.ConsultationDTO;
import com.gi.billingservice.model.dto.response.InvoiceDTO;
import com.gi.billingservice.model.entity.Invoice;
import com.gi.billingservice.model.enums.InvoiceStatus;
import com.gi.billingservice.model.enums.PaymentMethod;
import com.gi.billingservice.repository.InvoiceRepository;
import com.gi.billingservice.service.BillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceDTO generateInvoice(Long consultationId, BigDecimal amount) {
        // Mock consultation details
        ConsultationDTO consultation = new ConsultationDTO();
        consultation.setPatientId(1L); // 
        consultation.setCabinetId(1L); // Mock
        consultation.setDoctorId(1L); // Mock

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setPatientId(consultation.getPatientId());
        invoice.setConsultationId(consultationId);
        invoice.setCabinetId(consultation.getCabinetId());
        invoice.setDoctorId(consultation.getDoctorId());
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setAmount(amount);
        invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);

        Invoice saved = invoiceRepository.save(invoice);
        String patientName = "Mock Patient"; // Mock
        return invoiceMapper.toDTO(saved, patientName);
    }

    @Override
    public InvoiceDTO recordPayment(Long invoiceId, PaymentMethod paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.PENDING_PAYMENT) {
            throw new BusinessException("Invoice is not in pending payment status");
        }

        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.PAID);

        Invoice saved = invoiceRepository.save(invoice);
        String patientName = "Mock Patient"; // Mock
        return invoiceMapper.toDTO(saved, patientName);
    }

    @Override
    public InvoiceDTO cancelInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.CANCELLED);
        Invoice saved = invoiceRepository.save(invoice);
        String patientName = "Mock Patient"; // Mock
        return invoiceMapper.toDTO(saved, patientName);
    }

    @Override
    public InvoiceDTO findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        String patientName = "Mock Patient"; // Mock
        return invoiceMapper.toDTO(invoice, patientName);
    }

    @Override
    public List<InvoiceDTO> listInvoicesByPatient(Long patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);
        return invoices.stream()
                .map(invoice -> {
                    String patientName = "Mock Patient"; // Mock
                    return invoiceMapper.toDTO(invoice, patientName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generatePdf(Long invoiceId) {
        // TODO: Implement PDF generation
        throw new UnsupportedOperationException("PDF generation not implemented yet");
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}