package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.Enum.IntentType;

import java.time.LocalDate;
import java.time.LocalTime;

public interface NluService {

    IntentType detecterIntent(String message);

    LocalDate extraireDate(String message);

    LocalTime extraireHeure(String message);

    String extraireNom(String message);

    String extraireTelephone(String message);

    String extraireEmail(String message);

    Long extraireCabinetId(String message);

    String generateResponse(String userMessage, String context);
}
