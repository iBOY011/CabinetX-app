package com.gi.chatbotservice.Model.DTO;

import com.gi.chatbotservice.Model.Enum.BookingState;
import com.gi.chatbotservice.Model.Enum.IntentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private String sessionToken;

    private String reply;

    private IntentType detectedIntent;

    private BookingState bookingState;

    private List<String> creneauxDisponibles;

    private List<CabinetDTO> cabinets;

    private String confirmationMessage;

    private boolean requiresCabinetSelection;

    private boolean requiresCreneauSelection;

    private boolean requiresConfirmation;

    private BookingContextDTO bookingContext;
}
