package com.gi.chatbotservice.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gi.chatbotservice.Model.DTO.ClinicInfo;

@FeignClient(name = "clinic-service", path = "/api/clinics")
public interface ClinicClient {

    @GetMapping
    List<ClinicInfo> getAllClinics();

    @GetMapping("/active")
    List<ClinicInfo> getActiveClinics();

    @GetMapping("/{id}")
    ClinicInfo getClinicById(@PathVariable("id") Long id);
}
