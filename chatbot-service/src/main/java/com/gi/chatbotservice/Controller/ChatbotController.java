package com.gi.chatbotservice.Controller;

import com.gi.chatbotservice.Model.DTO.ChatMessageRequest;
import com.gi.chatbotservice.Model.DTO.ChatMessageResponse;
import com.gi.chatbotservice.Model.DTO.SessionResponse;
import com.gi.chatbotservice.Service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Create a new chat session when user opens the chatbot
     */
    @PostMapping("/session/start")
    public ResponseEntity<SessionResponse> startSession() {
        SessionResponse response = chatbotService.createSession();
        return ResponseEntity.ok(response);
    }

    /**
     * Close the session when user closes the site/chatbot
     */
    @PostMapping("/session/end")
    public ResponseEntity<Void> endSession(@RequestParam String sessionToken) {
        chatbotService.closeSession(sessionToken);
        return ResponseEntity.ok().build();
    }

    /**
     * Process a message from the user
     */
    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> traiterMessage(@RequestBody ChatMessageRequest request) {
        ChatMessageResponse response = chatbotService.traiterMessage(request);
        return ResponseEntity.ok(response);
    }
}
