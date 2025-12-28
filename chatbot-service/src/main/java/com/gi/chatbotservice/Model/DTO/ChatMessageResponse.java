package com.gi.chatbotservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long sessionId;

    private String reply;

    private List<String> creneauxDisponibles;

    private List<String> cabinets;
    
    private List<String> Medecins;

    private String confirmationMessage;
}
