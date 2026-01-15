package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.Enum.IntentType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NluServiceImpl implements NluService {

    @Override
    public IntentType detecterIntent(String message) {
        if (message == null) {
            return IntentType.AUTRE;
        }
        
        String lowerMessage = message.toLowerCase();
        
        // Check for greeting intent
        if (lowerMessage.matches(".*(bonjour|salut|hello|hi|coucou|bonsoir).*")) {
            return IntentType.GREETING;
        }

        // Check for help intent
        if (lowerMessage.matches(".*(aide|help|comment|quoi faire|que peux).*")) {
            return IntentType.HELP;
        }

        // Check for available slots intent
        if (lowerMessage.contains("disponibilit") || 
            lowerMessage.contains("libre") || 
            lowerMessage.contains("créneau") ||
            lowerMessage.contains("creneau") ||
            lowerMessage.contains("horaire") ||
            lowerMessage.contains("plage")) {
            return IntentType.AVAILABLE_SLOTS;
        }
        
        // Check for doctors info intent
        if (lowerMessage.contains("médecin") || 
            lowerMessage.contains("medecin") || 
            lowerMessage.contains("docteur") ||
            lowerMessage.contains("dr ") ||
            lowerMessage.contains("praticien") ||
            lowerMessage.contains("spécialiste")) {
            return IntentType.DOCTORS_INFO;
        }

        // Check for clinics info intent
        if (lowerMessage.contains("cabinet") ||
            lowerMessage.contains("clinique") ||
            lowerMessage.contains("adresse") ||
            lowerMessage.contains("information") ||
            lowerMessage.contains("contact") ||
            lowerMessage.contains("où") ||
            lowerMessage.contains("localisation")) {
            return IntentType.CLINICS_INFO;
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
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = Integer.parseInt(matcher.group(3));
            return LocalDate.of(year, month, day);
        }
        
        // Check for "demain"
        if (message.toLowerCase().contains("demain")) {
            return LocalDate.now().plusDays(1);
        }
        
        // Check for "aujourd'hui"
        if (message.toLowerCase().contains("aujourd")) {
            return LocalDate.now();
        }
        
        return null;
    }

    @Override
    public Long extractClinicId(String message) {
        if (message == null) {
            return null;
        }
        
        // Pattern for "cabinet X" or "clinique X" where X is a number
        Pattern pattern = Pattern.compile("(?:cabinet|clinique|numéro|numero|#)\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        
        // Also check for standalone numbers that might be clinic IDs
        Pattern numberPattern = Pattern.compile("\\b(\\d{1,4})\\b");
        Matcher numberMatcher = numberPattern.matcher(message);
        
        if (numberMatcher.find()) {
            // Only return if it's a small number (likely a clinic ID)
            long id = Long.parseLong(numberMatcher.group(1));
            if (id < 1000) {
                return id;
            }
        }
        
        return null;
    }
}
