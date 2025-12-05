package com.gi.chatbotservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {

    private String sessionToken;

    private LocalDateTime createdAt;

    private boolean active;

    private String welcomeMessage;
}
