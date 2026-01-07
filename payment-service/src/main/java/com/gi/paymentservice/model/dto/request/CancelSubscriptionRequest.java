package com.gi.paymentservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelSubscriptionRequest {
    private Long cabinetId;
    private Boolean cancelAtPeriodEnd;
}
