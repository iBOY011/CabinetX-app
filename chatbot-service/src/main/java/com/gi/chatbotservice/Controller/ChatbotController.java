package com.gi.chatbotservice.Controller;

import com.gi.chatbotservice.Model.DTO.ChatMessageRequest;
import com.gi.chatbotservice.Model.DTO.ChatMessageResponse;
import com.gi.chatbotservice.Service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> traiterMessage(@RequestBody ChatMessageRequest request) {
        ChatMessageResponse response = chatbotService.traiterMessage(request);
        return ResponseEntity.ok(response);
    }
}
