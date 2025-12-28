package com.gi.billingservice.model.dto.response;

import com.gi.billingservice.model.enums.InvoiceStatus;
import com.gi.billingservice.model.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceDTO {

    private Long id;
    private String invoiceNumber;
    private Long patientId;
    private Long consultationId;
    private LocalDateTime invoiceDate;
    private BigDecimal amount;
    private InvoiceStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private String patientName;
}