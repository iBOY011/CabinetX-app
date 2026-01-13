package com.gi.billingservice.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.gi.billingservice.service.BillingService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final ConsultationClient consultationClient;

    @Override
    public InvoiceDTO generateInvoice(Long consultationId, BigDecimal amount) {
        // Fetch consultation details from consultation service
        ConsultationDTO consultation = consultationClient.getConsultation(consultationId);
        if (consultation == null) {
            throw new ResourceNotFoundException("Consultation not found with id: " + consultationId);
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setPatientId(consultation.getPatientId());
        invoice.setConsultationId(consultationId);
        invoice.setCabinetId(consultation.getCabinetId());
        invoice.setDoctorId(consultation.getMedecinId());
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
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Mock data for patient and consultation
        String patientName = "Jean Dupont";
        String patientEmail = "jean.dupont@email.com";
        String patientPhone = "+33 6 12 34 56 78";
        String patientAddress = "123 Rue de la Santé, 75014 Paris, France";
        String doctorName = "Dr. Marie Martin";
        String doctorSpecialty = "Médecin Généraliste";
        String cabinetName = "Cabinet Médical CabinetX";
        String cabinetAddress = "45 Avenue des Soins, 75008 Paris, France";
        String cabinetPhone = "+33 1 23 45 67 89";
        String consultationType = "Consultation Générale";
        String consultationNotes = "Examen de routine - Patient en bonne santé";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Header - Cabinet Information
            Paragraph header = new Paragraph(cabinetName)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(header);

            Paragraph cabinetInfo = new Paragraph(cabinetAddress + "\n" + "Tél: " + cabinetPhone)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY);
            document.add(cabinetInfo);

            document.add(new Paragraph("\n"));

            // Invoice Title
            Paragraph invoiceTitle = new Paragraph("FACTURE")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(invoiceTitle);

            Paragraph invoiceNumber = new Paragraph("N° " + invoice.getInvoiceNumber())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(invoiceNumber);

            document.add(new Paragraph("\n"));

            // Two-column layout for patient and invoice info
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Patient Information
            Cell patientCell = new Cell()
                    .add(new Paragraph("INFORMATIONS PATIENT").setBold().setFontSize(12))
                    .add(new Paragraph("Nom: " + patientName).setFontSize(10))
                    .add(new Paragraph("Email: " + patientEmail).setFontSize(10))
                    .add(new Paragraph("Tél: " + patientPhone).setFontSize(10))
                    .add(new Paragraph("Adresse: " + patientAddress).setFontSize(10))
                    .setBorder(null);
            infoTable.addCell(patientCell);

            // Invoice Details
            Cell invoiceCell = new Cell()
                    .add(new Paragraph("DÉTAILS FACTURE").setBold().setFontSize(12))
                    .add(new Paragraph("Date: " + invoice.getInvoiceDate().format(dateFormatter)).setFontSize(10))
                    .add(new Paragraph("Statut: " + getStatusLabel(invoice.getStatus())).setFontSize(10))
                    .add(new Paragraph("Médecin: " + doctorName).setFontSize(10))
                    .add(new Paragraph("Spécialité: " + doctorSpecialty).setFontSize(10))
                    .setBorder(null)
                    .setTextAlignment(TextAlignment.RIGHT);
            infoTable.addCell(invoiceCell);

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Services Table
            Paragraph servicesTitle = new Paragraph("PRESTATIONS")
                    .setBold()
                    .setFontSize(14)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(servicesTitle);

            Table servicesTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Header row
            servicesTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            servicesTable.addHeaderCell(new Cell().add(new Paragraph("Montant").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            // Service row
            servicesTable.addCell(new Cell().add(new Paragraph(consultationType + "\n" + consultationNotes).setFontSize(10)));
            servicesTable.addCell(new Cell().add(new Paragraph(invoice.getAmount() + " €").setFontSize(10))
                    .setTextAlignment(TextAlignment.RIGHT));

            // Total row
            servicesTable.addCell(new Cell().add(new Paragraph("TOTAL").setBold()));
            servicesTable.addCell(new Cell().add(new Paragraph(invoice.getAmount() + " €").setBold())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            document.add(servicesTable);
            document.add(new Paragraph("\n"));

            // Payment Information
            if (invoice.getPaymentMethod() != null && invoice.getPaymentDate() != null) {
                Paragraph paymentInfo = new Paragraph("INFORMATIONS DE PAIEMENT")
                        .setBold()
                        .setFontSize(14)
                        .setFontColor(ColorConstants.DARK_GRAY);
                document.add(paymentInfo);

                Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                        .setWidth(UnitValue.createPercentValue(100));
                paymentTable.addCell(new Cell().add(new Paragraph("Mode de paiement: " + getPaymentMethodLabel(invoice.getPaymentMethod())).setFontSize(10)).setBorder(null));
                paymentTable.addCell(new Cell().add(new Paragraph("Date de paiement: " + invoice.getPaymentDate().format(dateFormatter)).setFontSize(10)).setBorder(null).setTextAlignment(TextAlignment.RIGHT));
                document.add(paymentTable);
                document.add(new Paragraph("\n"));
            }

            // Footer
            Paragraph footer = new Paragraph("Merci de votre confiance.\n" +
                    "Pour toute question concernant cette facture, veuillez nous contacter.")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY);
            document.add(footer);

            Paragraph generatedAt = new Paragraph("Document généré le " + LocalDateTime.now().format(dateFormatter))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY);
            document.add(generatedAt);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Failed to generate PDF: " + e.getMessage());
        }
    }

    private String getStatusLabel(InvoiceStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> "En attente de paiement";
            case PAID -> "Payée";
            case CANCELLED -> "Annulée";
        };
    }

    private String getPaymentMethodLabel(PaymentMethod method) {
        return switch (method) {
            case CASH -> "Espèces";
            case CREDIT_CARD -> "Carte bancaire";
            case INSURANCE -> "Assurance";
            case CHECK -> "Chèque";
        };
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}