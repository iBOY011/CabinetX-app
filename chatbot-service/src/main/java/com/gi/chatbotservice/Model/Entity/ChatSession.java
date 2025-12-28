package com.gi.chatbotservice.Model.Entity;

import com.gi.chatbotservice.Model.Enum.IntentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDebut;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private IntentType dernierIntent;
}
