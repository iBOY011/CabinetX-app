package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.Enum.IntentType;

import java.time.LocalDate;

public interface NluService {

    IntentType detecterIntent(String message);

    LocalDate extraireDate(String message);

    String extraireNom(String message);

    String extraireTelephone(String message);
}
