package com.gi.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clinic-service")
public interface ClinicClient {

    @PatchMapping("/api/clinics/{id}/activate")
    void activateClinic(@PathVariable("id") Long id);

    @PatchMapping("/api/clinics/{id}/deactivate")
    void deactivateClinic(@PathVariable("id") Long id);
}
