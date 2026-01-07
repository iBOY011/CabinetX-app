package com.gi.paymentservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSessionRequest {

    private String planId;
    private String priceId;
    private Long cabinetId;
    private String customerEmail;
    private String customerName;
    private String successUrl;
    private String cancelUrl;
}
