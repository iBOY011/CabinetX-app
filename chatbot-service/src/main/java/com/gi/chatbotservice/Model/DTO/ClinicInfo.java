package com.gi.chatbotservice.Model.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicInfo {

    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private String address;
    private String logoUrl;
    private String status;
    private LocalDate serviceEndDate;
}
