package com.gi.chatbotservice.Service;

import java.time.LocalDate;

import com.gi.chatbotservice.Model.Enum.IntentType;

public interface NluService {

    IntentType detecterIntent(String message);

    LocalDate extraireDate(String message);

    Long extractClinicId(String message);
}
