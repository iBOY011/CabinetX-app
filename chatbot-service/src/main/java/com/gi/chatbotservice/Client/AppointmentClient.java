package com.gi.chatbotservice.Client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gi.chatbotservice.Model.DTO.AppointmentInfo;

@FeignClient(name = "appointment-service", path = "/api/appointments")
public interface AppointmentClient {

    @GetMapping("/by-date")
    List<AppointmentInfo> getAppointmentsByDate(@RequestParam("date") LocalDate date,
                                                 @RequestParam("cabinetId") Long cabinetId);
}
