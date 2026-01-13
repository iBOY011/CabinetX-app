package com.gi.paymentservice.model.entity;

import com.gi.paymentservice.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "billing_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long cabinetId;

    @Column(length = 120)
    private String stripeCustomerId;

    @Column(length = 120, unique = true)
    private String stripeSubscriptionId;

    @Column(length = 120)
    private String checkoutSessionId;

    @Column(length = 120)
    private String priceId;

    @Column(length = 160)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    private LocalDateTime currentPeriodStart;

    private LocalDateTime currentPeriodEnd;

    private Boolean autoRenew;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
