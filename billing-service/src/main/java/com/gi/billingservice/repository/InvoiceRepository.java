package com.gi.billingservice.repository;

import com.gi.billingservice.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByConsultationId(Long consultationId);

    List<Invoice> findByPatientId(Long patientId);

    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}