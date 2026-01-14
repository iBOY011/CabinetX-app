package com.gi.billingservice.service;

import com.gi.billingservice.model.dto.response.InvoiceDTO;
import com.gi.billingservice.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public interface BillingService {

    InvoiceDTO generateInvoice(Long consultationId, BigDecimal amount);

    InvoiceDTO generateInvoiceFromConsultation(Long consultationId, Long patientId, Long cabinetId, Long doctorId, BigDecimal amount);

    InvoiceDTO recordPayment(Long invoiceId, PaymentMethod paymentMethod);

    InvoiceDTO cancelInvoice(Long invoiceId);

    InvoiceDTO findById(Long id);

    InvoiceDTO findByConsultationId(Long consultationId);

    InvoiceDTO updateAmount(Long invoiceId, BigDecimal amount);

    List<InvoiceDTO> listInvoicesByPatient(Long patientId);

    byte[] generatePdf(Long invoiceId);
}