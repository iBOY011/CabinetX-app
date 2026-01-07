package com.gi.paymentservice.model.dto.response;

import com.gi.paymentservice.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingSubscriptionDTO {
    private Long cabinetId;
    private String stripeSubscriptionId;
    private String priceId;
    private String planName;
    private SubscriptionStatus status;
    private Boolean autoRenew;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
}
