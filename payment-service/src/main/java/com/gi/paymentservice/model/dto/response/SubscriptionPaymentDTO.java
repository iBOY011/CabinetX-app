package com.gi.paymentservice.model.dto.response;

import com.gi.paymentservice.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPaymentDTO {

    private Long id;
    private Long subscriptionId;
    private Long cabinetId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String redirectUrl;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}