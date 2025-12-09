package com.gi.billingservice.mapper;

import com.gi.billingservice.model.dto.response.InvoiceDTO;
import com.gi.billingservice.model.entity.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    public InvoiceDTO toDTO(Invoice invoice, String patientName) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setPatientId(invoice.getPatientId());
        dto.setConsultationId(invoice.getConsultationId());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setAmount(invoice.getAmount());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setPaymentDate(invoice.getPaymentDate());
        dto.setPatientName(patientName);
        return dto;
    }
}