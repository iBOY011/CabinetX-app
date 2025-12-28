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
        
        // Check for availability intent
        if (lowerMessage.contains("disponibilit") || 
            lowerMessage.contains("libre") || 
            lowerMessage.contains("créneau") ||
            lowerMessage.contains("creneau") ||
            lowerMessage.contains("horaire")) {
            return IntentType.CONSULTER_DISPONIBILITES;
        }
        
        // Check for appointment booking intent
        if (lowerMessage.contains("rendez-vous") || 
            lowerMessage.contains("rdv") || 
            lowerMessage.contains("réserver") ||
            lowerMessage.contains("reserver") ||
            lowerMessage.contains("prendre") ||
            lowerMessage.contains("booking")) {
            return IntentType.PRENDRE_RDV;
        }

        // Check for cabinet info intent
        if (lowerMessage.contains("cabinet") ||
            lowerMessage.contains("adresse") ||
            lowerMessage.contains("information") ||
            lowerMessage.contains("contact")) {
            return IntentType.CABINETS_INFO;
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
    public String extraireNom(String message) {
        if (message == null) {
            return null;
        }
        
        // Pattern for "je m'appelle X" or "mon nom est X"
        Pattern pattern = Pattern.compile(
            "(?:je m'appelle|mon nom est|je suis|c'est)\\s+([A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)?)", 
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
        
        // Pattern for phone numbers like "06 12 34 56 78" or "0612345678"
        Pattern pattern = Pattern.compile("(0[67][\\s.-]?\\d{2}[\\s.-]?\\d{2}[\\s.-]?\\d{2}[\\s.-]?\\d{2})");
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            return matcher.group(1).replaceAll("[\\s.-]", "");
        }
        
        return null;
    }
}
