package com.gi.chatbotservice.Model.DTO;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotInfo {

    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public SlotInfo(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = true;
    }
}
