package com.gi.billingservice.service;

import com.gi.billingservice.model.dto.response.InvoiceDTO;
import com.gi.billingservice.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public interface BillingService {

    InvoiceDTO generateInvoice(Long consultationId, BigDecimal amount);

    InvoiceDTO recordPayment(Long invoiceId, PaymentMethod paymentMethod);

    InvoiceDTO cancelInvoice(Long invoiceId);

    InvoiceDTO findById(Long id);

    List<InvoiceDTO> listInvoicesByPatient(Long patientId);

    byte[] generatePdf(Long invoiceId);
}