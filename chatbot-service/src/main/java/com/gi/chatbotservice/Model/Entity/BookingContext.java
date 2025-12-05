package com.gi.chatbotservice.Model.Entity;

import com.gi.chatbotservice.Model.Enum.BookingState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "booking_contexts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Long cabinetId;

    private String cabinetName;

    private Long doctorId;

    private String doctorName;

    private String nomPatient;

    private String telephone;

    private String email;

    private LocalDate dateSouhaitee;

    private LocalTime creneauChoisi;

    @Enumerated(EnumType.STRING)
    private BookingState etat;

    private Long rendezVousId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        etat = BookingState.INITIAL;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean hasCriticalInfo() {
        return nomPatient != null && !nomPatient.trim().isEmpty() &&
               telephone != null && !telephone.trim().isEmpty() &&
               creneauChoisi != null &&
               cabinetId != null &&
               doctorId != null;
    }

    public String getMissingInfoMessage() {
        StringBuilder missing = new StringBuilder();
        if (cabinetId == null) {
            missing.append("- Cabinet médical\n");
        }
        if (nomPatient == null || nomPatient.trim().isEmpty()) {
            missing.append("- Votre nom\n");
        }
        if (telephone == null || telephone.trim().isEmpty()) {
            missing.append("- Votre numéro de téléphone\n");
        }
        if (dateSouhaitee == null) {
            missing.append("- Date souhaitée\n");
        }
        if (creneauChoisi == null) {
            missing.append("- Horaire souhaité\n");
        }
        return missing.toString();
    }
}
