package com.gi.chatbotservice.Client;

import com.gi.chatbotservice.Model.DTO.DoctorInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {

    @GetMapping("/clinic/{clinicId}")
    List<DoctorInfo> getUsersByClinic(@PathVariable("clinicId") Long clinicId);

    @GetMapping("/by-cabinet-and-role")
    List<DoctorInfo> getDoctorsByClinic(@RequestParam("cabinetId") Long cabinetId, 
                                         @RequestParam("role") String role);

    @GetMapping("/{id}")
    DoctorInfo getUserById(@PathVariable("id") Long id);
}
