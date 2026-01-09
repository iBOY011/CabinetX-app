package com.gi.billingservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gi.billingservice.model.dto.response.InvoiceDTO;
import com.gi.billingservice.model.enums.PaymentMethod;
import com.gi.billingservice.service.BillingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceDTO> generateInvoice(@RequestParam Long consultationId, @RequestParam BigDecimal amount) {
        InvoiceDTO invoice = billingService.generateInvoice(consultationId, amount);
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/invoices/{id}/payment")
    public ResponseEntity<InvoiceDTO> recordPayment(@PathVariable Long id, @RequestParam PaymentMethod paymentMethod) {
        InvoiceDTO invoice = billingService.recordPayment(id, paymentMethod);
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/invoices/{id}/cancel")
    public ResponseEntity<InvoiceDTO> cancelInvoice(@PathVariable Long id) {
        InvoiceDTO invoice = billingService.cancelInvoice(id);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Long id) {
        InvoiceDTO invoice = billingService.findById(id);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/patients/{patientId}/invoices")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByPatient(@PathVariable Long patientId) {
        List<InvoiceDTO> invoices = billingService.listInvoicesByPatient(patientId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        byte[] pdf = billingService.generatePdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf")
                .header("Content-Length", String.valueOf(pdf.length))
                .body(pdf);
    }
}