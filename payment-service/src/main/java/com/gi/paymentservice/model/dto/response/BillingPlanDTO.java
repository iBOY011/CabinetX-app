package com.gi.paymentservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingPlanDTO {
    private String planId;
    private String priceId;
    private String name;
    private Long amount;
    private String currency;
    private String interval;
    private String description;
}
