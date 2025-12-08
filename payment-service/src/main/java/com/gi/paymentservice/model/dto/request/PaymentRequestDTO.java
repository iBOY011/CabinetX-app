package com.gi.paymentservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    private Long subscriptionId;
    private Long cabinetId;
    private Long doctorId;
    private BigDecimal amount;
}