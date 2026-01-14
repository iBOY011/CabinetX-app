package com.gi.consultationservice.client;

import java.time.LocalDate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "prescription-service")
public interface PrescriptionClient {
    
    @GetMapping("/api/prescriptions/count")
    Long getPrescriptionCount(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam(value = "clinicId", required = false) Long clinicId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );
}
