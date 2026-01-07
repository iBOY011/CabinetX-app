package com.gi.paymentservice.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCustomerDTO {
    private Long cabinetId;
    private String stripeCustomerId;
    private String email;
    private String name;
    private LocalDateTime createdAt;
}
