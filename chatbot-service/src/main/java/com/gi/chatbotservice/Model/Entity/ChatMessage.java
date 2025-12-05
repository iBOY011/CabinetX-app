package com.gi.chatbotservice.Model.Entity;

import com.gi.chatbotservice.Model.Enum.IntentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private boolean estUtilisateur;

    @Column(length = 2000)
    private String contenu;

    @Enumerated(EnumType.STRING)
    private IntentType intentDetecte;

    private LocalDateTime dateEnvoi;
}
