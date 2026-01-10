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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceDTO generateInvoice(Long consultationId, BigDecimal amount) {
        // For backward compatibility, try to get consultation details
        // In a real implementation, this would call a consultation service
        // For now, we'll use mock data but log a warning
        LOGGER.warn("Using generateInvoice with mock data for consultationId: {}. Consider using generateInvoiceFromConsultation instead.", consultationId);

        ConsultationDTO consultation = new ConsultationDTO();
        consultation.setPatientId(1L); // Mock
        consultation.setCabinetId(1L); // Mock
        consultation.setDoctorId(1L); // Mock

        return generateInvoiceFromConsultation(consultationId, consultation.getPatientId(),
                consultation.getCabinetId(), consultation.getDoctorId(), amount);
    }

    @Override
    public InvoiceDTO generateInvoiceFromConsultation(Long consultationId, Long patientId, Long cabinetId, Long doctorId, BigDecimal amount) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setPatientId(patientId);
        invoice.setConsultationId(consultationId);
        invoice.setCabinetId(cabinetId);
        invoice.setDoctorId(doctorId);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setAmount(amount);
        invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);

        Invoice saved = invoiceRepository.save(invoice);
        String patientName = "Patient " + patientId; // In real implementation, this would come from patient service
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