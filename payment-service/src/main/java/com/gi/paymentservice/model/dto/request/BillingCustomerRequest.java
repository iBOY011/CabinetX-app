package com.gi.paymentservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCustomerRequest {

    private Long cabinetId;
    private String email;
    private String name;
}
