package com.gi.chatbotservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gi.chatbotservice.Model.DTO.ChatMessageRequest;
import com.gi.chatbotservice.Model.DTO.ChatMessageResponse;
import com.gi.chatbotservice.Service.ChatbotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> traiterMessage(@RequestBody ChatMessageRequest request) {
        try {
            log.info("Received message request: sessionId={}, message={}", request.getSessionId(), request.getMessage());
            ChatMessageResponse response = chatbotService.traiterMessage(request);
            log.info("Response generated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            throw e;
        }
    }
}
