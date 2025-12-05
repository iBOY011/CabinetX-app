package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.Enum.IntentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class NluServiceImpl implements NluService {

    private final ChatModel chatModel;

    @Override
    public IntentType detecterIntent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return IntentType.AUTRE;
        }

        try {
            String prompt = """
                Analyse le message suivant et détermine l'intention de l'utilisateur.
                Réponds UNIQUEMENT avec l'un de ces mots-clés (en majuscules, sans explication):
                - GREETING : si c'est une salutation (bonjour, salut, hello, etc.)
                - PRENDRE_RDV : si l'utilisateur veut prendre/réserver un rendez-vous
                - CONSULTER_DISPONIBILITES : si l'utilisateur veut voir les créneaux disponibles
                - CONFIRMER_RDV : si l'utilisateur confirme un rendez-vous (oui, je confirme, c'est bon)
                - ANNULER_RDV : si l'utilisateur veut annuler un rendez-vous
                - CABINETS_INFO : si l'utilisateur demande des informations sur les cabinets
                - FOURNIR_INFO : si l'utilisateur fournit des informations personnelles (nom, téléphone, email, date)
                - AUTRE : pour tout autre message
                
                Message: "%s"
                
                Intention:
                """.formatted(message);

            ChatClient chatClient = ChatClient.create(chatModel);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            String cleanResponse = response.trim().toUpperCase().replaceAll("[^A-Z_]", "");
            log.info("Intent detection - Message: '{}', Detected: '{}'", message, cleanResponse);

            return switch (cleanResponse) {
                case "GREETING" -> IntentType.GREETING;
                case "PRENDRE_RDV", "PRENDRERENDEZ", "PRENDRERV" -> IntentType.PRENDRE_RDV;
                case "CONSULTER_DISPONIBILITES", "CONSULTERDISPONIBILITES" -> IntentType.CONSULTER_DISPONIBILITES;
                case "CONFIRMER_RDV", "CONFIRMERRDV" -> IntentType.CONFIRMER_RDV;
                case "ANNULER_RDV", "ANNULERRDV" -> IntentType.ANNULER_RDV;
                case "CABINETS_INFO", "CABINETSINFO" -> IntentType.CABINETS_INFO;
                case "FOURNIR_INFO", "FOURNIRINFO" -> IntentType.FOURNIR_INFO;
                default -> IntentType.AUTRE;
            };
        } catch (Exception e) {
            log.error("Error detecting intent with Gemini: {}", e.getMessage());
            return detectIntentFallback(message);
        }
    }

    private IntentType detectIntentFallback(String message) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.matches("^(bonjour|salut|hello|hi|bonsoir|hey).*")) {
            return IntentType.GREETING;
        }

        if (lowerMessage.contains("rendez-vous") || lowerMessage.contains("rdv") ||
            lowerMessage.contains("réserver") || lowerMessage.contains("reserver") ||
            lowerMessage.contains("prendre")) {
            return IntentType.PRENDRE_RDV;
        }

        if (lowerMessage.contains("disponibilit") || lowerMessage.contains("libre") ||
            lowerMessage.contains("créneau") || lowerMessage.contains("creneau") ||
            lowerMessage.contains("horaire")) {
            return IntentType.CONSULTER_DISPONIBILITES;
        }

        if (lowerMessage.contains("confirme") || lowerMessage.matches(".*(oui|ok|d'accord|parfait).*")) {
            return IntentType.CONFIRMER_RDV;
        }

        if (lowerMessage.contains("annuler") || lowerMessage.contains("annulation")) {
            return IntentType.ANNULER_RDV;
        }

        if (lowerMessage.contains("cabinet") || lowerMessage.contains("adresse") ||
            lowerMessage.contains("information") || lowerMessage.contains("contact")) {
            return IntentType.CABINETS_INFO;
        }

        if (extraireNom(message) != null || extraireTelephone(message) != null ||
            extraireDate(message) != null || extraireHeure(message) != null) {
            return IntentType.FOURNIR_INFO;
        }

        return IntentType.AUTRE;
    }

    @Override
    public LocalDate extraireDate(String message) {
        if (message == null) {
            return null;
        }

        // Pattern for dates like "15/01/2024" or "15-01-2024"
        Pattern pattern = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            try {
                int day = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int year = Integer.parseInt(matcher.group(3));
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                log.warn("Invalid date format: {}", e.getMessage());
            }
        }

        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("demain")) {
            return LocalDate.now().plusDays(1);
        }

        if (lowerMessage.contains("aujourd'hui") || lowerMessage.contains("aujourd")) {
            return LocalDate.now();
        }

        if (lowerMessage.contains("après-demain") || lowerMessage.contains("apres-demain") ||
            lowerMessage.contains("après demain")) {
            return LocalDate.now().plusDays(2);
        }

        return null;
    }

    @Override
    public LocalTime extraireHeure(String message) {
        if (message == null) {
            return null;
        }

        // Pattern for times like "14h30", "14:30", "14h"
        Pattern pattern = Pattern.compile("(\\d{1,2})[h:]?(\\d{2})?");
        Matcher matcher = pattern.matcher(message.toLowerCase());

        if (matcher.find()) {
            try {
                int hour = Integer.parseInt(matcher.group(1));
                int minute = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                    return LocalTime.of(hour, minute);
                }
            } catch (Exception e) {
                log.warn("Invalid time format: {}", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public String extraireNom(String message) {
        if (message == null) {
            return null;
        }

        Pattern pattern = Pattern.compile(
            "(?:je m'appelle|mon nom est|je suis|c'est|nom[:\\s]+)\\s*([A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)?)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    @Override
    public String extraireTelephone(String message) {
        if (message == null) {
            return null;
        }

        // Pattern for phone numbers
        Pattern pattern = Pattern.compile("(0[67][\\s.-]?\\d{2}[\\s.-]?\\d{2}[\\s.-]?\\d{2}[\\s.-]?\\d{2})");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).replaceAll("[\\s.-]", "");
        }

        // Pattern for phone numbers with +212
        Pattern pattern212 = Pattern.compile("(\\+212[\\s.-]?[67][\\s.-]?\\d{2}[\\s.-]?\\d{2}[\\s.-]?\\d{2}[\\s.-]?\\d{2})");
        Matcher matcher212 = pattern212.matcher(message);

        if (matcher212.find()) {
            return matcher212.group(1).replaceAll("[\\s.-]", "");
        }

        return null;
    }

    @Override
    public String extraireEmail(String message) {
        if (message == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    @Override
    public Long extraireCabinetId(String message) {
        if (message == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("cabinet[\\s#]*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }

        return null;
    }

    @Override
    public String generateResponse(String userMessage, String context) {
        try {
            String prompt = """
                Tu es un assistant virtuel professionnel et amical pour un cabinet médical au Maroc.
                
                Ton rôle:
                - Aider les patients à prendre des rendez-vous
                - Fournir des informations sur les cabinets et médecins
                - Répondre aux questions de manière claire et professionnelle
                - Être chaleureux, empathique et patient
                
                Style de communication:
                - Utilise un ton professionnel mais chaleureux
                - Sois concis mais complet dans tes réponses
                - Utilise des émojis appropriés (🏥 📅 👨‍⚕️ ⏰ 📞 📍) pour rendre le message plus lisible
                - Adapte ton niveau de langage au contexte médical
                - Parle en français
                
                Contexte de la conversation: %s
                
                Message de l'utilisateur: %s
                
                Génère une réponse naturelle et contextuelle. Sois direct et utile.
                """.formatted(context, userMessage);

            ChatClient chatClient = ChatClient.create(chatModel);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
                    
            log.info("AI Response generated - Context: '{}', Response length: {}", context, response.length());
            return response.trim();
        } catch (Exception e) {
            log.error("Error generating response with Gemini: {}", e.getMessage(), e);
            return "Je suis désolé, je rencontre un problème technique en ce moment. Pouvez-vous reformuler votre demande ou réessayer dans quelques instants ?";
        }
    }
}
