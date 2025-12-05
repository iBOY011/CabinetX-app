package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.DTO.*;
import com.gi.chatbotservice.Model.Entity.BookingContext;
import com.gi.chatbotservice.Model.Entity.ChatMessage;
import com.gi.chatbotservice.Model.Entity.ChatSession;
import com.gi.chatbotservice.Model.Enum.IntentType;

import java.time.LocalDate;
import java.util.List;

public interface IChatbotService {

    /**
     * Create a new chat session
     */
    SessionResponse createSession();

    /**
     * Close/terminate a chat session
     */
    void closeSession(String sessionToken);

    /**
     * Main entry point - processes a user message and returns a response
     */
    ChatMessageResponse traiterMessage(ChatMessageRequest request);

    /**
     * Get or create a chat session by token
     */
    ChatSession getSessionByToken(String sessionToken);

    /**
     * Save a user message to the database
     */
    ChatMessage saveUserMessage(Long sessionId, String contenu, IntentType intent);

    /**
     * Save a bot response message to the database
     */
    ChatMessage saveBotMessage(Long sessionId, String contenu);

    /**
     * Detect intent from user message
     */
    IntentType detectIntent(String message);

    /**
     * Update session with the last detected intent
     */
    void updateSessionIntent(ChatSession session, IntentType intent);

    /**
     * Process response based on detected intent
     */
    ChatMessageResponse processIntent(ChatSession session, ChatMessageRequest request, IntentType intent);

    /**
     * Get available time slots for a cabinet on a specific date
     */
    List<String> obtenirCreneauxDisponibles(Long cabinetId, Long doctorId, LocalDate date);

    /**
     * Reserve an appointment
     */
    Long reserverRendezVous(BookingContext context);

    /**
     * Get or create a booking context for a session
     */
    BookingContext getOrCreateBookingContext(Long sessionId);

    /**
     * Get booking context for a session if exists
     */
    BookingContext getBookingContext(Long sessionId);

    /**
     * Delete booking context if missing critical info
     */
    void deleteBookingContextIfInvalid(BookingContext context);

    /**
     * Get list of available cabinets with their doctors
     */
    List<CabinetDTO> getCabinetsWithDoctors();

    /**
     * Convert BookingContext to DTO
     */
    BookingContextDTO toBookingContextDTO(BookingContext context);
}
