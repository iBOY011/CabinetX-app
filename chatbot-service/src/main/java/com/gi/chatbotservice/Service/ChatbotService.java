package com.gi.chatbotservice.Service;

import com.gi.chatbotservice.Client.AppointmentClient;
import com.gi.chatbotservice.Client.ClinicClient;
import com.gi.chatbotservice.Client.UserClient;
import com.gi.chatbotservice.Model.DTO.*;
import com.gi.chatbotservice.Model.Entity.ChatMessage;
import com.gi.chatbotservice.Model.Entity.ChatSession;
import com.gi.chatbotservice.Model.Enum.IntentType;
import com.gi.chatbotservice.Repository.ChatMessageRepository;
import com.gi.chatbotservice.Repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService implements IChatbotService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GeminiService geminiService;
    private final ClinicClient clinicClient;
    private final UserClient userClient;
    private final AppointmentClient appointmentClient;

    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(17, 0);
    private static final int SLOT_DURATION_MINUTES = 30;

    @Override
    public ChatMessageResponse traiterMessage(ChatMessageRequest request) {
        try {
            log.info("Processing message: sessionId={}, message={}", request.getSessionId(), request.getMessage());
            
            // Step 1: Get or create session
            ChatSession session = getOrCreateSession(request.getSessionId());
            log.info("Session: {}", session.getId());

            // Step 2: Detect intent to gather relevant data
            IntentType intent = detectIntent(request.getMessage());
            log.info("Detected intent: {}", intent);

            // Step 3: Save user message
            saveUserMessage(session.getId(), request.getMessage(), intent);

            // Step 4: Update session with last intent
            updateSessionIntent(session, intent);

            // Step 5: Get conversation history for context
            List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderByDateEnvoiAsc(session.getId());

            // Step 6: Gather context data based on intent
            String contextInfo = gatherContextInfo(intent, request.getMessage());
            
            // Step 7: Generate response using Gemini
            String geminiResponse = geminiService.generateResponse(request.getMessage(), history, contextInfo);

            // Step 8: Build response object
            ChatMessageResponse response = buildResponse(session, intent, geminiResponse, request.getMessage());

            // Step 9: Save bot response
            saveBotMessage(session.getId(), response.getReply());

            log.info("Message processed successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            ChatMessageResponse errorResponse = new ChatMessageResponse();
            errorResponse.setReply("Désolé, une erreur s'est produite. Veuillez réessayer. 🙏");
            return errorResponse;
        }
    }

    /**
     * Gather context information from other services based on detected intent
     */
    private String gatherContextInfo(IntentType intent, String message) {
        StringBuilder context = new StringBuilder();
        
        try {
            switch (intent) {
                case CLINICS_INFO:
                    List<ClinicInfo> clinics = getClinicsInfo();
                    if (!clinics.isEmpty()) {
                        context.append("LISTE DES CABINETS DISPONIBLES:\n");
                        for (ClinicInfo clinic : clinics) {
                            context.append(String.format("- Cabinet #%d: %s\n", clinic.getId(), clinic.getName()));
                            context.append(String.format("  Spécialité: %s\n", clinic.getSpecialty()));
                            context.append(String.format("  Adresse: %s\n", clinic.getAddress()));
                            context.append(String.format("  Téléphone: %s\n", clinic.getPhone()));
                            context.append("\n");
                        }
                    }
                    break;

                case DOCTORS_INFO:
                    Long clinicIdForDoctors = extractClinicId(message);
                    if (clinicIdForDoctors != null) {
                        List<DoctorInfo> doctors = getDoctorsForClinic(clinicIdForDoctors);
                        if (!doctors.isEmpty()) {
                            context.append(String.format("MÉDECINS DU CABINET #%d:\n", clinicIdForDoctors));
                            for (DoctorInfo doctor : doctors) {
                                context.append(String.format("- Dr. %s %s\n", doctor.getFirstName(), doctor.getLastName()));
                                if (doctor.getSpecialty() != null) {
                                    context.append(String.format("  Spécialité: %s\n", doctor.getSpecialty()));
                                }
                                if (doctor.getPhone() != null) {
                                    context.append(String.format("  Téléphone: %s\n", doctor.getPhone()));
                                }
                                context.append("\n");
                            }
                        }
                    } else {
                        // Provide list of clinics so user can choose
                        List<ClinicInfo> allClinics = getClinicsInfo();
                        context.append("L'utilisateur demande des médecins mais n'a pas précisé le cabinet.\n");
                        context.append("CABINETS DISPONIBLES:\n");
                        for (ClinicInfo clinic : allClinics) {
                            context.append(String.format("- Cabinet #%d: %s\n", clinic.getId(), clinic.getName()));
                        }
                    }
                    break;

                case AVAILABLE_SLOTS:
                    Long clinicIdForSlots = extractClinicId(message);
                    LocalDate date = extractDate(message);
                    if (date == null) {
                        date = LocalDate.now().plusDays(1);
                    }
                    
                    if (clinicIdForSlots != null) {
                        List<SlotInfo> slots = getAvailableSlots(clinicIdForSlots, date);
                        context.append(String.format("CRÉNEAUX DISPONIBLES POUR LE CABINET #%d LE %s:\n", 
                                clinicIdForSlots, date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                        if (slots.isEmpty()) {
                            context.append("Aucun créneau disponible pour cette date.\n");
                        } else {
                            for (SlotInfo slot : slots) {
                                context.append(String.format("- %s - %s\n", slot.getStartTime(), slot.getEndTime()));
                            }
                        }
                    } else {
                        List<ClinicInfo> allClinics = getClinicsInfo();
                        context.append("L'utilisateur demande des disponibilités mais n'a pas précisé le cabinet.\n");
                        context.append("CABINETS DISPONIBLES:\n");
                        for (ClinicInfo clinic : allClinics) {
                            context.append(String.format("- Cabinet #%d: %s\n", clinic.getId(), clinic.getName()));
                        }
                    }
                    break;

                case GREETING:
                case HELP:
                    // For greeting/help, provide general info about available services
                    List<ClinicInfo> availableClinics = getClinicsInfo();
                    context.append("INFORMATIONS GÉNÉRALES:\n");
                    context.append(String.format("- Nombre de cabinets actifs: %d\n", availableClinics.size()));
                    if (!availableClinics.isEmpty()) {
                        context.append("- Cabinets: ");
                        context.append(String.join(", ", availableClinics.stream()
                                .map(ClinicInfo::getName).toList()));
                        context.append("\n");
                    }
                    break;

                default:
                    // For unknown intent, provide basic context
                    List<ClinicInfo> basicClinics = getClinicsInfo();
                    if (!basicClinics.isEmpty()) {
                        context.append("SERVICES DISPONIBLES:\n");
                        context.append("- Informations sur les cabinets médicaux\n");
                        context.append("- Liste des médecins par cabinet\n");
                        context.append("- Disponibilités et créneaux horaires\n");
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("Error gathering context: {}", e.getMessage());
            context.append("Note: Impossible de récupérer certaines informations en temps réel.\n");
        }
        
        return context.toString();
    }

    /**
     * Build response object with additional data based on intent
     */
    private ChatMessageResponse buildResponse(ChatSession session, IntentType intent, String geminiResponse, String message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setSessionId(session.getId());
        response.setReply(geminiResponse);

        try {
            switch (intent) {
                case CLINICS_INFO:
                    response.setClinics(getClinicsInfo());
                    break;

                case DOCTORS_INFO:
                    Long clinicIdForDoctors = extractClinicId(message);
                    if (clinicIdForDoctors != null) {
                        response.setDoctors(getDoctorsForClinic(clinicIdForDoctors));
                        response.setSelectedClinicId(clinicIdForDoctors);
                    } else {
                        response.setClinics(getClinicsInfo());
                    }
                    break;

                case AVAILABLE_SLOTS:
                    Long clinicIdForSlots = extractClinicId(message);
                    LocalDate date = extractDate(message);
                    if (date == null) {
                        date = LocalDate.now().plusDays(1);
                    }
                    if (clinicIdForSlots != null) {
                        response.setAvailableSlots(getAvailableSlots(clinicIdForSlots, date));
                        response.setSelectedClinicId(clinicIdForSlots);
                    } else {
                        response.setClinics(getClinicsInfo());
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            log.error("Error building response data: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public ChatSession getOrCreateSession(Long sessionId) {
        if (sessionId != null) {
            Optional<ChatSession> existing = chatSessionRepository.findById(sessionId);
            if (existing.isPresent() && existing.get().isActive()) {
                return existing.get();
            }
        }

        ChatSession newSession = new ChatSession();
        newSession.setDateDebut(LocalDateTime.now());
        newSession.setActive(true);
        return chatSessionRepository.save(newSession);
    }

    @Override
    public ChatMessage saveUserMessage(Long sessionId, String contenu, IntentType intent) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setEstUtilisateur(true);
        message.setContenu(contenu);
        message.setIntentDetecte(intent);
        message.setDateEnvoi(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    @Override
    public ChatMessage saveBotMessage(Long sessionId, String contenu) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setEstUtilisateur(false);
        message.setContenu(contenu);
        message.setDateEnvoi(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    @Override
    public IntentType detectIntent(String message) {
        if (message == null) return IntentType.AUTRE;
        
        String lower = message.toLowerCase();
        
        if (lower.matches(".*(bonjour|salut|hello|hi|coucou|bonsoir).*")) {
            return IntentType.GREETING;
        }
        if (lower.matches(".*(aide|help|comment|quoi faire|que peux).*")) {
            return IntentType.HELP;
        }
        if (lower.contains("disponibilit") || lower.contains("libre") || 
            lower.contains("créneau") || lower.contains("creneau") || 
            lower.contains("horaire") || lower.contains("plage")) {
            return IntentType.AVAILABLE_SLOTS;
        }
        if (lower.contains("médecin") || lower.contains("medecin") || 
            lower.contains("docteur") || lower.contains("dr ") || 
            lower.contains("praticien") || lower.contains("spécialiste")) {
            return IntentType.DOCTORS_INFO;
        }
        if (lower.contains("cabinet") || lower.contains("clinique") || 
            lower.contains("adresse") || lower.contains("information") || 
            lower.contains("contact") || lower.contains("où") || 
            lower.contains("localisation") || lower.contains("telephone") ||
            lower.contains("téléphone") || lower.contains("numero")) {
            return IntentType.CLINICS_INFO;
        }
        
        return IntentType.AUTRE;
    }

    @Override
    public void updateSessionIntent(ChatSession session, IntentType intent) {
        session.setDernierIntent(intent);
        chatSessionRepository.save(session);
    }

    @Override
    public ChatMessageResponse processIntent(ChatSession session, String message, IntentType intent) {
        // This method is now delegated to gatherContextInfo + Gemini
        return null;
    }

    @Override
    public List<ClinicInfo> getClinicsInfo() {
        try {
            return clinicClient.getActiveClinics();
        } catch (Exception e) {
            log.error("Error fetching clinics: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<DoctorInfo> getDoctorsForClinic(Long clinicId) {
        try {
            return userClient.getDoctorsByClinic(clinicId, "MEDECIN");
        } catch (Exception e) {
            log.error("Error fetching doctors: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<SlotInfo> getAvailableSlots(Long clinicId, LocalDate date) {
        try {
            List<AppointmentInfo> appointments = appointmentClient.getAppointmentsByDate(date, clinicId);
            List<SlotInfo> allSlots = generateAllSlots();

            for (SlotInfo slot : allSlots) {
                for (AppointmentInfo apt : appointments) {
                    if (slotsOverlap(slot.getStartTime(), slot.getEndTime(), 
                            apt.getHeureDebut(), apt.getHeureFin())) {
                        slot.setAvailable(false);
                        break;
                    }
                }
            }

            return allSlots.stream().filter(SlotInfo::isAvailable).toList();
        } catch (Exception e) {
            log.error("Error fetching slots: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private Long extractClinicId(String message) {
        if (message == null) return null;
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?:cabinet|clinique|numéro|numero|#)\\s*(\\d+)", 
                java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        
        // Check for standalone small numbers
        java.util.regex.Pattern numberPattern = java.util.regex.Pattern.compile("\\b(\\d{1,3})\\b");
        java.util.regex.Matcher numberMatcher = numberPattern.matcher(message);
        
        if (numberMatcher.find()) {
            long id = Long.parseLong(numberMatcher.group(1));
            if (id > 0 && id < 100) {
                return id;
            }
        }
        
        return null;
    }

    private LocalDate extractDate(String message) {
        if (message == null) return null;
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = Integer.parseInt(matcher.group(3));
            return LocalDate.of(year, month, day);
        }
        
        String lower = message.toLowerCase();
        if (lower.contains("demain")) {
            return LocalDate.now().plusDays(1);
        }
        if (lower.contains("aujourd")) {
            return LocalDate.now();
        }
        
        return null;
    }

    private List<SlotInfo> generateAllSlots() {
        List<SlotInfo> slots = new ArrayList<>();
        LocalTime current = WORK_START;

        while (current.plusMinutes(SLOT_DURATION_MINUTES).isBefore(WORK_END) ||
                current.plusMinutes(SLOT_DURATION_MINUTES).equals(WORK_END)) {
            slots.add(new SlotInfo(current, current.plusMinutes(SLOT_DURATION_MINUTES)));
            current = current.plusMinutes(SLOT_DURATION_MINUTES);
        }

        return slots;
    }

    private boolean slotsOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
