package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Model.DTO.*;
import com.gi.chatbotservice.Model.Entity.BookingContext;
import com.gi.chatbotservice.Model.Entity.ChatMessage;
import com.gi.chatbotservice.Model.Entity.ChatSession;
import com.gi.chatbotservice.Model.Enum.BookingState;
import com.gi.chatbotservice.Model.Enum.IntentType;
import com.gi.chatbotservice.Repository.BookingContextRepository;
import com.gi.chatbotservice.Repository.ChatMessageRepository;
import com.gi.chatbotservice.Repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService implements IChatbotService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BookingContextRepository bookingContextRepository;
    private final NluService nluService;

    @Override
    @Transactional
    public SessionResponse createSession() {
        ChatSession session = new ChatSession();
        session.setSessionToken(UUID.randomUUID().toString());
        session.setActive(true);
        session.setDateDebut(LocalDateTime.now());
        
        ChatSession saved = chatSessionRepository.save(session);
        log.info("Created new session with token: {}", saved.getSessionToken());
        
        // Generate AI-powered welcome message
        String aiContext = "Un nouvel utilisateur vient de se connecter. Accueille-le chaleureusement en tant qu'assistant virtuel d'un cabinet médical. " +
                          "Présente brièvement les services disponibles: prise de rendez-vous, consultation des disponibilités, et informations sur les cabinets. " +
                          "Sois professionnel mais amical.";
        String welcomeMessage = nluService.generateResponse("Nouvelle session", aiContext);
        
        return SessionResponse.builder()
                .sessionToken(saved.getSessionToken())
                .createdAt(saved.getDateDebut())
                .active(true)
                .welcomeMessage(welcomeMessage)
                .build();
    }

    @Override
    @Transactional
    public void closeSession(String sessionToken) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findBySessionToken(sessionToken);
        if (sessionOpt.isPresent()) {
            ChatSession session = sessionOpt.get();
            session.setActive(false);
            session.setDateFin(LocalDateTime.now());
            chatSessionRepository.save(session);
            
            // Clean up incomplete booking context
            Optional<BookingContext> contextOpt = bookingContextRepository.findBySessionId(session.getId());
            if (contextOpt.isPresent()) {
                BookingContext context = contextOpt.get();
                if (context.getEtat() != BookingState.CONFIRME) {
                    bookingContextRepository.delete(context);
                    log.info("Deleted incomplete booking context for session: {}", sessionToken);
                }
            }
            
            log.info("Closed session with token: {}", sessionToken);
        }
    }

    @Override
    @Transactional
    public ChatMessageResponse traiterMessage(ChatMessageRequest request) {
        // Step 1: Get session by token
        ChatSession session = getSessionByToken(request.getSessionToken());
        if (session == null || !session.isActive()) {
            String aiContext = "La session de l'utilisateur a expiré. Explique-lui poliment qu'il doit rafraîchir la page pour démarrer une nouvelle conversation.";
            String aiResponse = nluService.generateResponse("Session expirée", aiContext);
            
            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .build();
        }

        // Step 2: Detect intent
        IntentType intent = detectIntent(request.getMessage());
        log.info("Detected intent: {} for message: {}", intent, request.getMessage());

        // Step 3: Save user message
        saveUserMessage(session.getId(), request.getMessage(), intent);

        // Step 4: Update session with last intent
        updateSessionIntent(session, intent);

        // Step 5: Process based on intent and booking context
        ChatMessageResponse response = processIntent(session, request, intent);
        response.setSessionToken(session.getSessionToken());
        response.setDetectedIntent(intent);

        // Step 6: Save bot response
        saveBotMessage(session.getId(), response.getReply());

        return response;
    }

    @Override
    public ChatSession getSessionByToken(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }
        return chatSessionRepository.findBySessionTokenAndActiveTrue(sessionToken).orElse(null);
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public void updateSessionIntent(ChatSession session, IntentType intent) {
        session.setDernierIntent(intent);
        chatSessionRepository.save(session);
    }

    @Override
    @Transactional
    public ChatMessageResponse processIntent(ChatSession session, ChatMessageRequest request, IntentType intent) {
        String message = request.getMessage();
        BookingContext existingContext = getBookingContext(session.getId());

        // If the user is just greeting, always respond with a greeting,
        // even if there is an ongoing booking context.
        if (intent == IntentType.GREETING) {
            return handleGreeting(session, message);
        }

        // Check if user wants to cancel a confirmed appointment
        if (intent == IntentType.ANNULER_RDV && existingContext != null 
                && existingContext.getEtat() == BookingState.CONFIRME) {
            String aiContext = "L'utilisateur veut annuler un rendez-vous déjà confirmé (référence #" + existingContext.getRendezVousId() + 
                              "). Explique poliment qu'il ne peut pas annuler via le chatbot et qu'il doit contacter directement le cabinet par téléphone. " +
                              "Sois compréhensif et professionnel.";
            String aiResponse = nluService.generateResponse(message, aiContext);
            
            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .bookingState(existingContext.getEtat())
                    .bookingContext(toBookingContextDTO(existingContext))
                    .build();
        }

        // If there's an existing booking context, continue the booking flow
        if (existingContext != null && existingContext.getEtat() != BookingState.CONFIRME) {
            return continueBookingFlow(session, request, existingContext, intent);
        }

        // Process based on intent
        return switch (intent) {
            case GREETING -> handleGreeting(session, message);
            case PRENDRE_RDV -> handlePrendreRdv(session, message);
            case CONSULTER_DISPONIBILITES -> handleConsulterDisponibilites(session, message);
            case CABINETS_INFO -> handleCabinetsInfo(session);
            case FOURNIR_INFO -> handleFournirInfo(session, message);
            default -> handleAutre(session, message);
        };
    }

    private ChatMessageResponse handleGreeting(ChatSession session, String userMessage) {
        String context = "L'utilisateur vient de saluer avec le message suivant : '" + userMessage + "'. " +
                "Réponds amicalement, éventuellement en reprenant sa salutation, et présente brièvement les services disponibles : " +
                "prise de rendez-vous, consultation des disponibilités et informations sur les cabinets.";
        String aiResponse = nluService.generateResponse(userMessage, context);
        
        return ChatMessageResponse.builder()
                .reply(aiResponse)
                .build();
    }

    private ChatMessageResponse handlePrendreRdv(ChatSession session, String message) {
        // Create new booking context
        BookingContext context = getOrCreateBookingContext(session.getId());
        context.setEtat(BookingState.SELECTING_CABINET);
        bookingContextRepository.save(context);

        // Extract any info from the initial message
        extractAndUpdateInfo(context, message);

        // Get cabinets list
        List<CabinetDTO> cabinets = getCabinetsWithDoctors();
        
        String cabinetInfo = formatCabinetsForAI(cabinets);
        String aiContext = "L'utilisateur veut prendre un rendez-vous. Voici les cabinets disponibles: " + cabinetInfo + 
                           ". Demande-lui de choisir un cabinet de manière amicale et professionnelle.";
        String aiResponse = nluService.generateResponse(message, aiContext);

        return ChatMessageResponse.builder()
                .reply(aiResponse)
                .cabinets(cabinets)
                .bookingState(BookingState.SELECTING_CABINET)
                .requiresCabinetSelection(true)
                .bookingContext(toBookingContextDTO(context))
                .build();
    }

    private ChatMessageResponse handleConsulterDisponibilites(ChatSession session, String message) {
        // Check if there's an active booking context
        BookingContext context = getBookingContext(session.getId());
        
        if (context == null) {
            // Create a new booking context for viewing availabilities
            context = getOrCreateBookingContext(session.getId());
            context.setEtat(BookingState.SELECTING_CABINET);
            bookingContextRepository.save(context);

            List<CabinetDTO> cabinets = getCabinetsWithDoctors();
            String cabinetInfo = formatCabinetsForAI(cabinets);
            String aiContext = "L'utilisateur veut consulter les disponibilités. Voici les cabinets disponibles: " + cabinetInfo + 
                              ". Demande-lui de choisir un cabinet pour voir les disponibilités.";
            String aiResponse = nluService.generateResponse(message, aiContext);
            
            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .cabinets(cabinets)
                    .bookingState(BookingState.SELECTING_CABINET)
                    .requiresCabinetSelection(true)
                    .bookingContext(toBookingContextDTO(context))
                    .build();
        }

        // If cabinet is selected, show availabilities
        if (context.getCabinetId() != null) {
            context.setEtat(BookingState.EN_SELECTION_CRENEAU);
            bookingContextRepository.save(context);

            LocalDate date = nluService.extraireDate(message);
            if (date == null) {
                date = LocalDate.now().plusDays(1);
            }
            context.setDateSouhaitee(date);
            bookingContextRepository.save(context);

            List<String> creneaux = obtenirCreneauxDisponibles(context.getCabinetId(), context.getDoctorId(), date);
            String aiContext = "L'utilisateur veut voir les disponibilités pour le " + date + " au cabinet " + context.getCabinetName() + 
                              ". Voici les créneaux disponibles: " + String.join(", ", creneaux) + 
                              ". Présente-les de manière claire et amicale.";
            String aiResponse = nluService.generateResponse(message, aiContext);
            
            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .creneauxDisponibles(creneaux)
                    .bookingState(BookingState.EN_SELECTION_CRENEAU)
                    .requiresCreneauSelection(true)
                    .bookingContext(toBookingContextDTO(context))
                    .build();
        }

        List<CabinetDTO> cabinets = getCabinetsWithDoctors();
        String cabinetInfo = formatCabinetsForAI(cabinets);
        String aiContext = "L'utilisateur veut voir les disponibilités mais n'a pas encore choisi de cabinet. Voici les cabinets: " + cabinetInfo + 
                          ". Rappelle-lui gentiment de sélectionner un cabinet.";
        String aiResponse = nluService.generateResponse(message, aiContext);
        
        return ChatMessageResponse.builder()
                .reply(aiResponse)
                .cabinets(cabinets)
                .requiresCabinetSelection(true)
                .bookingContext(toBookingContextDTO(context))
                .build();
    }

    private ChatMessageResponse handleCabinetsInfo(ChatSession session) {
        List<CabinetDTO> cabinets = getCabinetsWithDoctors();
        
        String cabinetDetails = formatCabinetsForAI(cabinets);
        String aiContext = "L'utilisateur demande des informations sur les cabinets médicaux. Voici les détails: " + cabinetDetails + 
                           ". Présente ces informations de manière claire et engageante, puis demande s'il souhaite prendre rendez-vous.";
        String aiResponse = nluService.generateResponse("Informations sur les cabinets", aiContext);

        return ChatMessageResponse.builder()
                .reply(aiResponse)
                .cabinets(cabinets)
                .build();
    }

    private ChatMessageResponse handleFournirInfo(ChatSession session, String message) {
        BookingContext context = getBookingContext(session.getId());
        
        if (context == null) {
            String aiContext = "L'utilisateur fournit des informations mais il n'y a pas de demande de rendez-vous en cours. Demande poliment s'il souhaite prendre un rendez-vous.";
            String aiResponse = nluService.generateResponse(message, aiContext);
            
            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .build();
        }

        // Extract and update info
        extractAndUpdateInfo(context, message);
        
        return continueBookingFlow(session, null, context, IntentType.FOURNIR_INFO);
    }

    private ChatMessageResponse handleAutre(ChatSession session, String message) {
        String aiResponse = nluService.generateResponse(message, "Conversation générale");
        return ChatMessageResponse.builder()
                .reply(aiResponse)
                .build();
    }

    private ChatMessageResponse continueBookingFlow(ChatSession session, ChatMessageRequest request, 
                                                     BookingContext context, IntentType intent) {
        String message = request != null ? request.getMessage() : "";

        // Handle cabinet selection from request
        if (request != null && request.getSelectedCabinetId() != null) {
            context.setCabinetId(request.getSelectedCabinetId());
            // Find cabinet name
            List<CabinetDTO> cabinets = getCabinetsWithDoctors();
            cabinets.stream()
                    .filter(c -> c.getId().equals(request.getSelectedCabinetId()))
                    .findFirst()
                    .ifPresent(c -> context.setCabinetName(c.getNom()));
            
            if (request.getSelectedDoctorId() != null) {
                context.setDoctorId(request.getSelectedDoctorId());
                cabinets.stream()
                        .flatMap(c -> c.getMedecins() != null ? c.getMedecins().stream() : java.util.stream.Stream.empty())
                        .filter(m -> m.getId().equals(request.getSelectedDoctorId()))
                        .findFirst()
                        .ifPresent(m -> context.setDoctorName("Dr. " + m.getPrenom() + " " + m.getNom()));
            }
            
            context.setEtat(BookingState.EN_SELECTION_CRENEAU);
            bookingContextRepository.save(context);
        }

        // Handle creneau selection from request
        if (request != null && request.getSelectedCreneau() != null) {
            LocalTime time = nluService.extraireHeure(request.getSelectedCreneau());
            if (time != null) {
                context.setCreneauChoisi(time);
                context.setEtat(BookingState.COLLECTING_INFO);
                bookingContextRepository.save(context);
            }
        }

        // Extract info from message
        extractAndUpdateInfo(context, message);

        // Determine next step based on current state
        return switch (context.getEtat()) {
            case INITIAL -> {
                context.setEtat(BookingState.SELECTING_CABINET);
                bookingContextRepository.save(context);
                List<CabinetDTO> cabinets = getCabinetsWithDoctors();
                
                String cabinetInfo = formatCabinetsForAI(cabinets);
                String aiContext = "L'utilisateur commence le processus de prise de rendez-vous. Voici les cabinets disponibles: " + cabinetInfo + ". Demande-lui de sélectionner un cabinet.";
                String aiResponse = nluService.generateResponse(message, aiContext);
                
                yield ChatMessageResponse.builder()
                        .reply(aiResponse)
                        .cabinets(cabinets)
                        .bookingState(BookingState.SELECTING_CABINET)
                        .requiresCabinetSelection(true)
                        .bookingContext(toBookingContextDTO(context))
                        .build();
            }
            case SELECTING_CABINET -> {
                if (context.getCabinetId() == null) {
                    List<CabinetDTO> cabinets = getCabinetsWithDoctors();
                    String cabinetInfo = formatCabinetsForAI(cabinets);
                    String aiContext = "L'utilisateur doit sélectionner un cabinet médical. Voici les options: " + cabinetInfo + ". Rappelle-lui gentiment de faire son choix.";
                    String aiResponse = nluService.generateResponse(message, aiContext);
                    
                    yield ChatMessageResponse.builder()
                            .reply(aiResponse)
                            .cabinets(cabinets)
                            .bookingState(BookingState.SELECTING_CABINET)
                            .requiresCabinetSelection(true)
                            .bookingContext(toBookingContextDTO(context))
                            .build();
                }
                // Cabinet selected, move to slot selection
                context.setEtat(BookingState.EN_SELECTION_CRENEAU);
                bookingContextRepository.save(context);
                yield continueBookingFlow(session, request, context, intent);
            }
            case EN_SELECTION_CRENEAU -> {
                if (context.getDateSouhaitee() == null) {
                    String aiContext = "L'utilisateur a choisi le cabinet " + context.getCabinetName() + ". Demande-lui maintenant pour quelle date il souhaite prendre rendez-vous. Donne des exemples comme 'demain' ou une date précise.";
                    String aiResponse = nluService.generateResponse(message, aiContext);
                    
                    yield ChatMessageResponse.builder()
                            .reply(aiResponse)
                            .bookingState(BookingState.EN_SELECTION_CRENEAU)
                            .bookingContext(toBookingContextDTO(context))
                            .build();
                }
                if (context.getCreneauChoisi() == null) {
                    List<String> creneaux = obtenirCreneauxDisponibles(
                            context.getCabinetId(), 
                            context.getDoctorId(), 
                            context.getDateSouhaitee());
                    
                    String aiContext = "L'utilisateur veut un rendez-vous le " + context.getDateSouhaitee() + " chez " + context.getCabinetName() + 
                                      ". Voici les créneaux disponibles: " + String.join(", ", creneaux) + ". Présente-les de manière claire et demande-lui de choisir.";
                    String aiResponse = nluService.generateResponse(message, aiContext);
                    
                    yield ChatMessageResponse.builder()
                            .reply(aiResponse)
                            .creneauxDisponibles(creneaux)
                            .bookingState(BookingState.EN_SELECTION_CRENEAU)
                            .requiresCreneauSelection(true)
                            .bookingContext(toBookingContextDTO(context))
                            .build();
                }
                // Slot selected, move to info collection
                context.setEtat(BookingState.COLLECTING_INFO);
                bookingContextRepository.save(context);
                yield continueBookingFlow(session, request, context, intent);
            }
            case COLLECTING_INFO -> {
                String missingInfo = context.getMissingInfoMessage();
                if (!missingInfo.isEmpty()) {
                    String aiContext = "L'utilisateur prend rendez-vous mais il manque des informations: " + missingInfo + 
                                      ". Demande ces informations de manière amicale et professionnelle.";
                    String aiResponse = nluService.generateResponse(message, aiContext);
                    
                    yield ChatMessageResponse.builder()
                            .reply(aiResponse)
                            .bookingState(BookingState.COLLECTING_INFO)
                            .bookingContext(toBookingContextDTO(context))
                            .build();
                }
                
                // Check for confirmation intent
                if (intent == IntentType.CONFIRMER_RDV || 
                    (message != null && message.toLowerCase().matches(".*(oui|confirme|ok|d'accord|parfait).*"))) {
                    yield handleConfirmation(session, context);
                }
                
                // All info collected, ask for confirmation
                String summary = "Cabinet: " + context.getCabinetName() + 
                               ", Médecin: " + (context.getDoctorName() != null ? context.getDoctorName() : "Non spécifié") +
                               ", Date: " + context.getDateSouhaitee() +
                               ", Heure: " + context.getCreneauChoisi() +
                               ", Patient: " + context.getNomPatient() +
                               ", Téléphone: " + context.getTelephone();
                               
                String aiContext = "Toutes les informations sont collectées pour le rendez-vous. Voici le récapitulatif: " + summary + 
                                  ". Présente ce récapitulatif de manière claire avec des emojis et demande confirmation.";
                String aiResponse = nluService.generateResponse(message, aiContext);
                
                yield ChatMessageResponse.builder()
                        .reply(aiResponse)
                        .bookingState(BookingState.COLLECTING_INFO)
                        .requiresConfirmation(true)
                        .bookingContext(toBookingContextDTO(context))
                        .build();
            }
            case CONFIRME -> {
                String aiContext = "Le rendez-vous de l'utilisateur est déjà confirmé avec la référence #" + context.getRendezVousId() + 
                                  ". Rappelle-lui cela et demande s'il a besoin d'autre chose.";
                String aiResponse = nluService.generateResponse(message, aiContext);
                
                yield ChatMessageResponse.builder()
                        .reply(aiResponse)
                        .bookingState(BookingState.CONFIRME)
                        .bookingContext(toBookingContextDTO(context))
                        .build();
            }
        };
    }

    private ChatMessageResponse handleConfirmation(ChatSession session, BookingContext context) {
        if (!context.hasCriticalInfo()) {
            String missing = context.getMissingInfoMessage();
            String aiContext = "L'utilisateur veut confirmer son rendez-vous mais il manque des informations essentielles: " + missing + 
                              ". Explique poliment qu'on ne peut pas confirmer sans ces informations.";
            String aiResponse = nluService.generateResponse("confirmation", aiContext);
            
            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .bookingState(context.getEtat())
                    .bookingContext(toBookingContextDTO(context))
                    .build();
        }

        // Create the appointment
        Long rdvId = reserverRendezVous(context);
        context.setRendezVousId(rdvId);
        context.setEtat(BookingState.CONFIRME);
        bookingContextRepository.save(context);
        
        String summary = "Cabinet: " + context.getCabinetName() + 
                       ", Médecin: " + (context.getDoctorName() != null ? context.getDoctorName() : "Non spécifié") +
                       ", Date: " + context.getDateSouhaitee() +
                       ", Heure: " + context.getCreneauChoisi() +
                       ", Patient: " + context.getNomPatient() +
                       ", Téléphone: " + context.getTelephone() +
                       ", Référence: #" + rdvId;

        String aiContext = "Le rendez-vous a été confirmé avec succès! Voici le récapitulatif complet: " + summary + 
                          ". Félicite l'utilisateur, présente le récapitulatif avec des emojis de manière professionnelle et chaleureuse, et termine par un message de remerciement.";
        String aiResponse = nluService.generateResponse("confirmation réussie", aiContext);

        return ChatMessageResponse.builder()
                .reply(aiResponse)
                .confirmationMessage("Rendez-vous confirmé - Référence #" + rdvId)
                .bookingState(BookingState.CONFIRME)
                .bookingContext(toBookingContextDTO(context))
                .build();
    }

    private void extractAndUpdateInfo(BookingContext context, String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String nom = nluService.extraireNom(message);
        if (nom != null && context.getNomPatient() == null) {
            context.setNomPatient(nom);
        }

        String telephone = nluService.extraireTelephone(message);
        if (telephone != null && context.getTelephone() == null) {
            context.setTelephone(telephone);
        }

        String email = nluService.extraireEmail(message);
        if (email != null && context.getEmail() == null) {
            context.setEmail(email);
        }

        LocalDate date = nluService.extraireDate(message);
        if (date != null && context.getDateSouhaitee() == null) {
            context.setDateSouhaitee(date);
        }

        LocalTime heure = nluService.extraireHeure(message);
        if (heure != null && context.getCreneauChoisi() == null) {
            context.setCreneauChoisi(heure);
        }

        bookingContextRepository.save(context);
    }

    @Override
    public List<String> obtenirCreneauxDisponibles(Long cabinetId, Long doctorId, LocalDate date) {
        // TODO: Call appointment-service to get real available slots
        // This is a placeholder implementation
        List<String> creneaux = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);
        
        while (start.isBefore(end)) {
            creneaux.add(start.toString());
            start = start.plusMinutes(30);
        }
        
        return creneaux;
    }

    @Override
    @Transactional
    public Long reserverRendezVous(BookingContext context) {
        // TODO: Call appointment-service to create the actual appointment
        // This is a placeholder implementation that returns a generated ID
        log.info("Creating appointment for patient: {} at cabinet: {} on {} at {}",
                context.getNomPatient(), context.getCabinetId(), 
                context.getDateSouhaitee(), context.getCreneauChoisi());
        
        return System.currentTimeMillis() % 100000; // Simple ID generation
    }

    @Override
    @Transactional
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

    @Override
    public BookingContext getBookingContext(Long sessionId) {
        return bookingContextRepository.findBySessionId(sessionId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteBookingContextIfInvalid(BookingContext context) {
        if (context != null && !context.hasCriticalInfo() && context.getEtat() != BookingState.CONFIRME) {
            bookingContextRepository.delete(context);
            log.info("Deleted invalid booking context: {}", context.getId());
        }
    }

    @Override
    public List<CabinetDTO> getCabinetsWithDoctors() {
        // TODO: Call clinic-service to get real cabinets and doctors
        // This is a placeholder implementation
        return List.of(
                CabinetDTO.builder()
                        .id(1L)
                        .nom("Cabinet Médical Centre Ville")
                        .adresse("123 Avenue Mohammed V, Casablanca")
                        .telephone("05 22 12 34 56")
                        .medecins(List.of(
                                MedecinDTO.builder().id(1L).nom("Bennani").prenom("Ahmed").specialite("Médecine Générale").cabinetId(1L).build(),
                                MedecinDTO.builder().id(2L).nom("El Fassi").prenom("Fatima").specialite("Pédiatrie").cabinetId(1L).build()
                        ))
                        .build(),
                CabinetDTO.builder()
                        .id(2L)
                        .nom("Cabinet Médical Maarif")
                        .adresse("45 Rue de France, Maarif, Casablanca")
                        .telephone("05 22 98 76 54")
                        .medecins(List.of(
                                MedecinDTO.builder().id(3L).nom("Chraibi").prenom("Karim").specialite("Cardiologie").cabinetId(2L).build(),
                                MedecinDTO.builder().id(4L).nom("Alaoui").prenom("Sara").specialite("Dermatologie").cabinetId(2L).build()
                        ))
                        .build(),
                CabinetDTO.builder()
                        .id(3L)
                        .nom("Cabinet Médical Anfa")
                        .adresse("78 Boulevard Anfa, Casablanca")
                        .telephone("05 22 45 67 89")
                        .medecins(List.of(
                                MedecinDTO.builder().id(5L).nom("Idrissi").prenom("Mohamed").specialite("Orthopédie").cabinetId(3L).build()
                        ))
                        .build()
        );
    }

    @Override
    public BookingContextDTO toBookingContextDTO(BookingContext context) {
        if (context == null) {
            return null;
        }
        return BookingContextDTO.builder()
                .id(context.getId())
                .cabinetId(context.getCabinetId())
                .cabinetName(context.getCabinetName())
                .doctorId(context.getDoctorId())
                .doctorName(context.getDoctorName())
                .nomPatient(context.getNomPatient())
                .telephone(context.getTelephone())
                .email(context.getEmail())
                .dateSouhaitee(context.getDateSouhaitee())
                .creneauChoisi(context.getCreneauChoisi())
                .etat(context.getEtat())
                .rendezVousId(context.getRendezVousId())
                .hasCriticalInfo(context.hasCriticalInfo())
                .missingInfo(context.getMissingInfoMessage())
                .build();
    }
    
    private String formatCabinetsForAI(List<CabinetDTO> cabinets) {
        StringBuilder info = new StringBuilder();
        for (CabinetDTO cabinet : cabinets) {
            info.append(cabinet.getNom())
                .append(" (ID: ").append(cabinet.getId()).append(")")
                .append(" - ").append(cabinet.getAdresse())
                .append(", Tél: ").append(cabinet.getTelephone());
            
            if (cabinet.getMedecins() != null && !cabinet.getMedecins().isEmpty()) {
                info.append(", Médecins: ");
                List<String> medecinNames = cabinet.getMedecins().stream()
                        .map(m -> "Dr. " + m.getPrenom() + " " + m.getNom() + " (" + m.getSpecialite() + ")")
                        .toList();
                info.append(String.join(", ", medecinNames));
            }
            info.append("; ");
        }
        return info.toString();
    }
}
