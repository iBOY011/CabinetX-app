package com.gi.chatbotservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gi.chatbotservice.Config.GeminiConfig;
import com.gi.chatbotservice.Model.Entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final WebClient geminiWebClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
            Tu es un assistant virtuel pour CabinetX, une plateforme de gestion de cabinets médicaux.
            Tu aides les utilisateurs publics (patients potentiels) à:
            - Trouver des informations sur les cabinets médicaux (nom, adresse, téléphone, spécialité)
            - Connaître les médecins disponibles dans chaque cabinet
            - Vérifier les disponibilités et créneaux horaires
            - Répondre aux questions générales sur les services de santé
            
            RÈGLES IMPORTANTES:
            1. Sois toujours poli, professionnel et empathique
            2. Réponds en français
            3. Si tu as des informations sur les cabinets/médecins dans le contexte, utilise-les pour répondre
            4. Si l'utilisateur demande des infos spécifiques que tu n'as pas, dis-lui poliment que tu vas chercher
            5. Ne donne JAMAIS de conseils médicaux - oriente vers un professionnel de santé
            6. Garde tes réponses concises mais informatives
            7. Utilise des emojis appropriés pour rendre la conversation plus conviviale
            
            FORMAT DES RÉPONSES:
            - Utilise des listes à puces pour les informations structurées
            - Mets en évidence les informations importantes
            - Termine souvent par une question pour guider l'utilisateur
            """;

    /**
     * Generate a response using Gemini API with conversation history
     */
    public String generateResponse(String userMessage, List<ChatMessage> conversationHistory, String contextInfo) {
        try {
            String requestBody = buildRequestBody(userMessage, conversationHistory, contextInfo);
            log.debug("Sending request to Gemini API");

            String response = geminiWebClient.post()
                    .uri("?key=" + geminiConfig.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .onErrorResume(e -> {
                        log.error("Error calling Gemini API: {}", e.getMessage());
                        if (e.getMessage() != null && e.getMessage().contains("429")) {
                            return Mono.just(createRateLimitResponse());
                        }
                        return Mono.just(createErrorResponse());
                    })
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Error generating response: {}", e.getMessage(), e);
            return "Désolé, je rencontre des difficultés techniques. Veuillez réessayer dans quelques instants. 🙏";
        }
    }

    private String createRateLimitResponse() {
        return "{\"rateLimited\": true}";
    }

    private String buildRequestBody(String userMessage, List<ChatMessage> history, String contextInfo) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();

            // Add system instruction
            ObjectNode systemInstruction = objectMapper.createObjectNode();
            ObjectNode systemParts = objectMapper.createObjectNode();
            systemParts.put("text", SYSTEM_PROMPT);
            systemInstruction.set("parts", objectMapper.createArrayNode().add(systemParts));
            root.set("systemInstruction", systemInstruction);

            // Add conversation history (last 10 messages for context)
            int startIndex = Math.max(0, history.size() - 10);
            for (int i = startIndex; i < history.size(); i++) {
                ChatMessage msg = history.get(i);
                ObjectNode contentNode = objectMapper.createObjectNode();
                contentNode.put("role", msg.isEstUtilisateur() ? "user" : "model");
                
                ObjectNode partNode = objectMapper.createObjectNode();
                partNode.put("text", msg.getContenu());
                contentNode.set("parts", objectMapper.createArrayNode().add(partNode));
                contents.add(contentNode);
            }

            // Add current user message with context
            ObjectNode userContent = objectMapper.createObjectNode();
            userContent.put("role", "user");
            
            String messageWithContext = userMessage;
            if (contextInfo != null && !contextInfo.isEmpty()) {
                messageWithContext = "CONTEXTE ACTUEL (informations des cabinets):\n" + contextInfo + 
                        "\n\nMESSAGE DE L'UTILISATEUR:\n" + userMessage;
            }
            
            ObjectNode userPart = objectMapper.createObjectNode();
            userPart.put("text", messageWithContext);
            userContent.set("parts", objectMapper.createArrayNode().add(userPart));
            contents.add(userContent);

            root.set("contents", contents);

            // Generation config
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1024);
            generationConfig.put("topP", 0.9);
            root.set("generationConfig", generationConfig);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error building request body: {}", e.getMessage());
            return "{}";
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // Check for rate limiting
            if (root.has("rateLimited")) {
                return "Bonjour ! 👋 Je suis actuellement très sollicité. Veuillez réessayer dans quelques secondes.\n\n" +
                       "En attendant, je peux vous aider avec:\n" +
                       "- 🏥 Informations sur nos cabinets\n" +
                       "- 👨‍⚕️ Liste des médecins disponibles\n" +
                       "- 📅 Créneaux horaires disponibles";
            }
            
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            
            // Check for error
            JsonNode error = root.path("error");
            if (!error.isMissingNode()) {
                log.error("Gemini API error: {}", error.path("message").asText());
                return "Désolé, une erreur s'est produite. Veuillez réessayer. 🙏";
            }
            
            return "Je n'ai pas pu générer une réponse. Pouvez-vous reformuler votre question? 🤔";
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            return "Désolé, je rencontre des difficultés techniques. 🙏";
        }
    }

    private String createErrorResponse() {
        return "{\"error\": {\"message\": \"API call failed\"}}";
    }
}
