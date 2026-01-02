package com.gi.prescriptionservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceCountResponse {
    private String correlationId;
    private long count;
}
