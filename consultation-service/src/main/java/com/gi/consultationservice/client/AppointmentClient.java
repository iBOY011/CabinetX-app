package com.gi.consultationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {
    
    @PutMapping("/api/appointments/updateStatus/{id}/TERMINE")
    void markAppointmentAsCompleted(@PathVariable("id") Long appointmentId);
}
