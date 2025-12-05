package com.gi.chatbotservice.Repository;

import com.gi.chatbotservice.Model.Entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    ChatSession save(ChatSession session);

    Optional<ChatSession> findById(Long id);

    Optional<ChatSession> findBySessionToken(String sessionToken);

    Optional<ChatSession> findBySessionTokenAndActiveTrue(String sessionToken);
}
