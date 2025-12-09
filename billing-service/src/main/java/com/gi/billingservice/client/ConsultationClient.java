package com.gi.billingservice.client;

import com.gi.billingservice.model.dto.request.ConsultationDTO;

public interface ConsultationClient {

    ConsultationDTO getConsultation(Long id);
}