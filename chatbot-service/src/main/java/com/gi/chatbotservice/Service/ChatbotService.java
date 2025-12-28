package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.DTO.ChatMessageRequest;
import com.gi.chatbotservice.Model.DTO.ChatMessageResponse;
import com.gi.chatbotservice.Model.Entity.BookingContext;
import com.gi.chatbotservice.Model.Entity.ChatMessage;
import com.gi.chatbotservice.Model.Entity.ChatSession;
import com.gi.chatbotservice.Model.Enum.BookingState;
import com.gi.chatbotservice.Model.Enum.IntentType;
import com.gi.chatbotservice.Repository.BookingContextRepository;
import com.gi.chatbotservice.Repository.ChatMessageRepository;
import com.gi.chatbotservice.Repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotService implements IChatbotService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BookingContextRepository bookingContextRepository;
    private final NluService nluService;

    @Override
    public ChatMessageResponse traiterMessage(ChatMessageRequest request) {
        // Step 1: Get or create session
        ChatSession session = getOrCreateSession(request.getSessionId());

        // Step 2: Detect intent
        IntentType intent = detectIntent(request.getMessage());

        // Step 3: Save user message
        saveUserMessage(session.getId(), request.getMessage(), intent);

        // Step 4: Update session with last intent
        updateSessionIntent(session, intent);

        // Step 5: Process based on intent
        ChatMessageResponse response = processIntent(session, request.getMessage(), intent);

        // Step 6: Save bot response
        saveBotMessage(session.getId(), response.getReply());

        return response;
    }

    @Override
    public ChatSession getOrCreateSession(Long sessionId) {
        if (sessionId != null) {
            Optional<ChatSession> existingSession = chatSessionRepository.findById(sessionId);
            if (existingSession.isPresent() && existingSession.get().isActive()) {
                return existingSession.get();
            }
        }

        // Create new session
        ChatSession newSession = new ChatSession();
        newSession.setDateDebut(LocalDateTime.now());
        newSession.setActive(true);
        return chatSessionRepository.save(newSession);
    }

    @Override
    public ChatMessage saveUserMessage(Long sessionId, String contenu, IntentType intent) {
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setEstUtilisateur(true);
        userMessage.setContenu(contenu);
        userMessage.setIntentDetecte(intent);
        userMessage.setDateEnvoi(LocalDateTime.now());
        return chatMessageRepository.save(userMessage);
    }

    @Override
    public ChatMessage saveBotMessage(Long sessionId, String contenu) {
        ChatMessage botMessage = new ChatMessage();
        botMessage.setSessionId(sessionId);
        botMessage.setEstUtilisateur(false);
        botMessage.setContenu(contenu);
        botMessage.setDateEnvoi(LocalDateTime.now());
        return chatMessageRepository.save(botMessage);
    }

    @Override
    public IntentType detectIntent(String message) {
        return nluService.detecterIntent(message);
    }

    @Override
    public void updateSessionIntent(ChatSession session, IntentType intent) {
        session.setDernierIntent(intent);
        chatSessionRepository.save(session);
    }

    @Override
    public ChatMessageResponse processIntent(ChatSession session, String message, IntentType intent) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setSessionId(session.getId());

        switch (intent) {
            case CONSULTER_DISPONIBILITES:
                return handleConsulterDisponibilites(session, message, response);
            case PRENDRE_RDV:
                return handlePrendreRdv(session, message, response);
            default:
                response.setReply("Je ne comprends pas votre demande. Comment puis-je vous aider ?");
                return response;
        }
    }

    @Override
    public List<String> obtenirCreneauxDisponibles(Long cabinetId, LocalDate date) {
        // TODO: Call appointment-service to get available slots
        // This is a placeholder implementation
        return List.of("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");
    }

    @Override
    public Long reserverRendezVous(BookingContext context) {
        // TODO: Call appointment-service to create the appointment
        // This is a placeholder implementation
        context.setEtat(BookingState.CONFIRME);
        bookingContextRepository.save(context);
        return context.getId();
    }

    @Override
    public BookingContext getOrCreateBookingContext(Long sessionId) {
        Optional<BookingContext> existing = bookingContextRepository.findBySessionId(sessionId);
        if (existing.isPresent()) {
            return existing.get();
        }

        BookingContext context = new BookingContext();
        context.setSessionId(sessionId);
        context.setEtat(BookingState.INITIAL);
        return bookingContextRepository.save(context);
    }

    private ChatMessageResponse handleConsulterDisponibilites(ChatSession session, String message, ChatMessageResponse response) {
        // Extract date from message
        LocalDate date = nluService.extraireDate(message);
        if (date == null) {
            date = LocalDate.now().plusDays(1); // Default to tomorrow
        }

        // Get or create booking context
        BookingContext context = getOrCreateBookingContext(session.getId());
        context.setDateSouhaitee(date);
        context.setEtat(BookingState.EN_SELECTION_CRENEAU);
        bookingContextRepository.save(context);

        // Get available slots
        List<String> creneaux = obtenirCreneauxDisponibles(context.getCabinetId(), date);
        response.setCreneauxDisponibles(creneaux);
        response.setReply("Voici les créneaux disponibles pour le " + date + ". Veuillez choisir un créneau.");

        return response;
    }

    private ChatMessageResponse handlePrendreRdv(ChatSession session, String message, ChatMessageResponse response) {
        BookingContext context = getOrCreateBookingContext(session.getId());

        // Extract information from message
        String nom = nluService.extraireNom(message);
        String telephone = nluService.extraireTelephone(message);
        LocalDate date = nluService.extraireDate(message);

        if (nom != null) {
            context.setNomPatient(nom);
        }
        if (telephone != null) {
            context.setTelephone(telephone);
        }
        if (date != null) {
            context.setDateSouhaitee(date);
        }

        // Check if we have all required information
        if (context.getNomPatient() == null || context.getTelephone() == null) {
            context.setEtat(BookingState.INITIAL);
            bookingContextRepository.save(context);
            response.setReply("Pour prendre rendez-vous, veuillez me fournir votre nom et numéro de téléphone.");
            return response;
        }

        if (context.getDateSouhaitee() == null) {
            bookingContextRepository.save(context);
            response.setReply("Pour quelle date souhaitez-vous prendre rendez-vous ?");
            return response;
        }

        if (context.getCreneauChoisi() == null) {
            context.setEtat(BookingState.EN_SELECTION_CRENEAU);
            bookingContextRepository.save(context);
            List<String> creneaux = obtenirCreneauxDisponibles(context.getCabinetId(), context.getDateSouhaitee());
            response.setCreneauxDisponibles(creneaux);
            response.setReply("Voici les créneaux disponibles. Veuillez en choisir un.");
            return response;
        }

        // All information available, create appointment
        Long rdvId = reserverRendezVous(context);
        context.setRendezVousId(rdvId);
        context.setEtat(BookingState.CONFIRME);
        bookingContextRepository.save(context);

        response.setConfirmationMessage("Votre rendez-vous a été confirmé pour le " + context.getDateSouhaitee() + 
                " à " + context.getCreneauChoisi() + ". Référence: " + rdvId);
        response.setReply("Rendez-vous confirmé !");

        return response;
    }
}
