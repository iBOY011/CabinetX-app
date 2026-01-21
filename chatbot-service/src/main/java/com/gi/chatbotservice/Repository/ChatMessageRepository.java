package com.gi.chatbotservice.Repository;

import com.gi.chatbotservice.Model.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    ChatMessage save(ChatMessage message);

    List<ChatMessage> findBySessionId(Long sessionId);
    
    List<ChatMessage> findBySessionIdOrderByDateEnvoiAsc(Long sessionId);
}

