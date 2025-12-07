package com.gi.medicalrecordservice.client;

import com.gi.medicalrecordservice.dto.ConsultationInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "consultation-service")
public interface ConsultationServiceClient {

    @GetMapping("/api/consultations/patients/{patientId}/historique")
    List<ConsultationInfo> getConsultationsByPatient(@PathVariable("patientId") Long patientId);
}
