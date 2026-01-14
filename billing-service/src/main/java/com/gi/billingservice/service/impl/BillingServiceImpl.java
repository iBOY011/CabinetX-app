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
import com.gi.billingservice.client.PatientClient;
import com.gi.billingservice.client.UserClient;
import com.gi.billingservice.client.ClinicClient;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final ConsultationClient consultationClient;
    private final PatientClient patientClient;
    private final UserClient userClient;
    private final ClinicClient clinicClient;

    @Override
    public InvoiceDTO generateInvoice(Long consultationId, BigDecimal amount) {
        // Fetch consultation details from consultation service
        ConsultationDTO consultation = consultationClient.getConsultation(consultationId);
        if (consultation == null) {
            throw new ResourceNotFoundException("Consultation not found with id: " + consultationId);
        }

        return generateInvoiceFromConsultation(consultationId, consultation.getPatientId(),
                consultation.getCabinetId(), consultation.getMedecinId(), amount);
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
        String patientName = patientClient.getPatientName(patientId);
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
        String patientName = patientClient.getPatientName(invoice.getPatientId());
        return invoiceMapper.toDTO(saved, patientName);
    }

    @Override
    public InvoiceDTO cancelInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.CANCELLED);
        Invoice saved = invoiceRepository.save(invoice);
        String patientName = patientClient.getPatientName(invoice.getPatientId());
        return invoiceMapper.toDTO(saved, patientName);
    }

    @Override
    public InvoiceDTO findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        String patientName = patientClient.getPatientName(invoice.getPatientId());
        return invoiceMapper.toDTO(invoice, patientName);
    }

    @Override
    public InvoiceDTO findByConsultationId(Long consultationId) {
        Invoice invoice = invoiceRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for consultation: " + consultationId));
        String patientName = patientClient.getPatientName(invoice.getPatientId());
        return invoiceMapper.toDTO(invoice, patientName);
    }

    @Override
    public InvoiceDTO updateAmount(Long invoiceId, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        
        invoice.setAmount(amount);
        Invoice saved = invoiceRepository.save(invoice);
        String patientName = patientClient.getPatientName(invoice.getPatientId());
        return invoiceMapper.toDTO(saved, patientName);
    }

    @Override
    public List<InvoiceDTO> listInvoicesByPatient(Long patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);
        String patientName = patientClient.getPatientName(patientId);
        return invoices.stream()
                .map(invoice -> invoiceMapper.toDTO(invoice, patientName))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generatePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Récupérer les vraies données du patient
        String patientName = patientClient.getPatientName(invoice.getPatientId());
        
        // Récupérer les données de consultation
        ConsultationDTO consultation = null;
        String consultationType = "Consultation Générale";
        String diagnostic = "Non spécifié";
        String traitement = "Non spécifié";
        String observations = "";
        Long medecinId = null;
        Long cabinetId = null;
        
        try {
            consultation = consultationClient.getConsultation(invoice.getConsultationId());
            if (consultation != null) {
                consultationType = consultation.getType() != null ? consultation.getType() : "Consultation Générale";
                diagnostic = consultation.getDiagnostic() != null ? consultation.getDiagnostic() : "Non spécifié";
                traitement = consultation.getTraitement() != null ? consultation.getTraitement() : "Non spécifié";
                medecinId = consultation.getMedecinId();
                cabinetId = consultation.getCabinetId();
                
                // Construire les observations/notes de consultation
                StringBuilder obsBuilder = new StringBuilder();
                if (diagnostic != null && !diagnostic.isEmpty() && !"Non spécifié".equals(diagnostic)) {
                    obsBuilder.append("Diagnostic: ").append(diagnostic);
                }
                if (traitement != null && !traitement.isEmpty() && !"Non spécifié".equals(traitement)) {
                    if (obsBuilder.length() > 0) obsBuilder.append("\n");
                    obsBuilder.append("Traitement: ").append(traitement);
                }
                observations = obsBuilder.toString();
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to fetch consultation details: {}", e.getMessage());
        }
        
        // Récupérer les informations du médecin
        String doctorName = "Dr. CabinetX";
        String doctorSpecialty = "Médecin Généraliste";
        if (medecinId != null) {
            LOGGER.info("Fetching doctor details for medecinId: {}", medecinId);
            try {
                UserClient.UserDTO doctor = userClient.getUser(medecinId);
                if (doctor != null) {
                    String fullName = doctor.getFullName();
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        doctorName = "Dr. " + fullName;
                    }
                    // La spécialité n'est pas dans le user-service, on garde la valeur par défaut
                    LOGGER.info("Doctor details fetched: name={}, role={}", doctorName, doctor.getRole());
                }
            } catch (Exception e) {
                LOGGER.warn("Unable to fetch doctor details: {}", e.getMessage());
            }
        }
        
        // Récupérer les informations du cabinet
        String cabinetName = "Cabinet Médical CabinetX";
        String cabinetAddress = "Casablanca, Maroc";
        String cabinetPhone = "+212 5 22 XX XX XX";
        if (cabinetId != null) {
            LOGGER.info("Fetching cabinet details for cabinetId: {}", cabinetId);
            try {
                ClinicClient.ClinicDTO clinic = clinicClient.getClinic(cabinetId);
                if (clinic != null) {
                    if (clinic.getName() != null && !clinic.getName().isEmpty()) {
                        cabinetName = clinic.getName();
                    }
                    if (clinic.getAddress() != null && !clinic.getAddress().isEmpty()) {
                        cabinetAddress = clinic.getAddress();
                    }
                    if (clinic.getPhone() != null && !clinic.getPhone().isEmpty()) {
                        cabinetPhone = clinic.getPhone();
                    }
                    LOGGER.info("Cabinet details fetched: name={}, address={}, phone={}", cabinetName, cabinetAddress, cabinetPhone);
                }
            } catch (Exception e) {
                LOGGER.warn("Unable to fetch clinic details: {}", e.getMessage());
            }
        }

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
                    .add(new Paragraph("ID Patient: #" + invoice.getPatientId()).setFontSize(10))
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
            String serviceDescription = consultationType;
            if (observations != null && !observations.isEmpty()) {
                serviceDescription += "\n" + observations;
            }
            servicesTable.addCell(new Cell().add(new Paragraph(serviceDescription).setFontSize(10)));
            servicesTable.addCell(new Cell().add(new Paragraph(invoice.getAmount() + " MAD").setFontSize(10))
                    .setTextAlignment(TextAlignment.RIGHT));

            // Total row
            servicesTable.addCell(new Cell().add(new Paragraph("TOTAL").setBold()));
            servicesTable.addCell(new Cell().add(new Paragraph(invoice.getAmount() + " MAD").setBold())
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