package com.gi.chatbotservice.Model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long sessionId;

    private String reply;

    private List<ClinicInfo> clinics;

    private List<DoctorInfo> doctors;

    private List<SlotInfo> availableSlots;

    private Long selectedClinicId;
}
