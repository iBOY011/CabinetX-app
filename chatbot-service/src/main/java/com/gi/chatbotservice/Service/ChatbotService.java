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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService implements IChatbotService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NluService nluService;
    private final ClinicClient clinicClient;
    private final UserClient userClient;
    private final AppointmentClient appointmentClient;

    // Default working hours for generating available slots
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(17, 0);
    private static final int SLOT_DURATION_MINUTES = 30;

    @Override
    public ChatMessageResponse traiterMessage(ChatMessageRequest request) {
        try {
            // Step 1: Get or create session
            log.info("Step 1: Getting or creating session for sessionId: {}", request.getSessionId());
            ChatSession session = getOrCreateSession(request.getSessionId());
            log.info("Session obtained: {}", session.getId());

            // Step 2: Detect intent
            log.info("Step 2: Detecting intent for message: {}", request.getMessage());
            IntentType intent = detectIntent(request.getMessage());
            log.info("Detected intent: {}", intent);

            // Step 3: Save user message
            log.info("Step 3: Saving user message");
            saveUserMessage(session.getId(), request.getMessage(), intent);

            // Step 4: Update session with last intent
            log.info("Step 4: Updating session intent");
            updateSessionIntent(session, intent);

            // Step 5: Process based on intent
            log.info("Step 5: Processing intent");
            ChatMessageResponse response = processIntent(session, request.getMessage(), intent);

            // Step 6: Save bot response
            log.info("Step 6: Saving bot response");
            saveBotMessage(session.getId(), response.getReply());

            log.info("Message processed successfully");
            return response;
        } catch (Exception e) {
            log.error("Error in traiterMessage: {}", e.getMessage(), e);
            ChatMessageResponse errorResponse = new ChatMessageResponse();
            errorResponse.setReply("Désolé, une erreur s'est produite. Veuillez réessayer.");
            return errorResponse;
        }
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
            case CLINICS_INFO:
                return handleClinicsInfo(response);
            case DOCTORS_INFO:
                return handleDoctorsInfo(session, message, response);
            case AVAILABLE_SLOTS:
                return handleAvailableSlots(session, message, response);
            case GREETING:
                return handleGreeting(response);
            case HELP:
                return handleHelp(response);
            default:
                response.setReply("Je ne comprends pas votre demande. Vous pouvez me demander:\n" +
                        "- Des informations sur nos cabinets\n" +
                        "- Les médecins d'un cabinet\n" +
                        "- Les créneaux disponibles pour un cabinet");
                return response;
        }
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
            log.error("Error fetching doctors for clinic {}: {}", clinicId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<SlotInfo> getAvailableSlots(Long clinicId, LocalDate date) {
        try {
            // Get existing appointments for the clinic on the given date
            List<AppointmentInfo> existingAppointments = appointmentClient.getAppointmentsByDate(date, clinicId);

            // Generate all possible slots
            List<SlotInfo> allSlots = generateAllSlots();

            // Mark slots as unavailable if they overlap with existing appointments
            for (SlotInfo slot : allSlots) {
                for (AppointmentInfo appointment : existingAppointments) {
                    if (slotsOverlap(slot.getStartTime(), slot.getEndTime(),
                            appointment.getHeureDebut(), appointment.getHeureFin())) {
                        slot.setAvailable(false);
                        break;
                    }
                }
            }

            // Return only available slots
            return allSlots.stream()
                    .filter(SlotInfo::isAvailable)
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching available slots for clinic {} on {}: {}", clinicId, date, e.getMessage());
            return new ArrayList<>();
        }
    }

    private ChatMessageResponse handleClinicsInfo(ChatMessageResponse response) {
        List<ClinicInfo> clinics = getClinicsInfo();

        if (clinics.isEmpty()) {
            response.setReply("Désolé, je n'ai pas pu récupérer les informations sur nos cabinets. Veuillez réessayer plus tard.");
        } else {
            StringBuilder sb = new StringBuilder("Voici la liste de nos cabinets actifs:\n\n");
            for (ClinicInfo clinic : clinics) {
                sb.append("📍 **").append(clinic.getName()).append("**\n");
                sb.append("   Spécialité: ").append(clinic.getSpecialty()).append("\n");
                sb.append("   Adresse: ").append(clinic.getAddress()).append("\n");
                sb.append("   Téléphone: ").append(clinic.getPhone()).append("\n\n");
            }
            sb.append("Pour plus d'informations sur les médecins ou les créneaux disponibles, précisez le nom du cabinet.");
            response.setReply(sb.toString());
            response.setClinics(clinics);
        }

        return response;
    }

    private ChatMessageResponse handleDoctorsInfo(ChatSession session, String message, ChatMessageResponse response) {
        // Try to extract clinic ID from message
        Long clinicId = nluService.extractClinicId(message);

        if (clinicId == null) {
            // Check if user previously asked about clinics
            List<ClinicInfo> clinics = getClinicsInfo();
            if (clinics.isEmpty()) {
                response.setReply("Pour voir les médecins, veuillez d'abord me préciser le cabinet.");
            } else {
                response.setReply("Pour voir les médecins, veuillez me préciser le numéro ou le nom du cabinet:\n");
                StringBuilder sb = new StringBuilder();
                for (ClinicInfo clinic : clinics) {
                    sb.append("- ").append(clinic.getId()).append(": ").append(clinic.getName()).append("\n");
                }
                response.setReply(response.getReply() + sb);
                response.setClinics(clinics);
            }
            return response;
        }

        List<DoctorInfo> doctors = getDoctorsForClinic(clinicId);

        if (doctors.isEmpty()) {
            response.setReply("Aucun médecin trouvé pour ce cabinet.");
        } else {
            StringBuilder sb = new StringBuilder("Voici les médecins du cabinet:\n\n");
            for (DoctorInfo doctor : doctors) {
                sb.append("👨‍⚕️ Dr. ").append(doctor.getFirstName()).append(" ").append(doctor.getLastName()).append("\n");
                if (doctor.getSpecialty() != null) {
                    sb.append("   Spécialité: ").append(doctor.getSpecialty()).append("\n");
                }
                if (doctor.getPhone() != null) {
                    sb.append("   Téléphone: ").append(doctor.getPhone()).append("\n");
                }
                sb.append("\n");
            }
            response.setReply(sb.toString());
            response.setDoctors(doctors);
            response.setSelectedClinicId(clinicId);
        }

        return response;
    }

    private ChatMessageResponse handleAvailableSlots(ChatSession session, String message, ChatMessageResponse response) {
        // Extract clinic ID and date from message
        Long clinicId = nluService.extractClinicId(message);
        LocalDate date = nluService.extraireDate(message);

        if (date == null) {
            date = LocalDate.now().plusDays(1); // Default to tomorrow
        }

        if (clinicId == null) {
            List<ClinicInfo> clinics = getClinicsInfo();
            if (clinics.isEmpty()) {
                response.setReply("Pour voir les créneaux disponibles, veuillez me préciser le cabinet.");
            } else {
                response.setReply("Pour voir les créneaux disponibles, veuillez me préciser le numéro du cabinet:\n");
                StringBuilder sb = new StringBuilder();
                for (ClinicInfo clinic : clinics) {
                    sb.append("- ").append(clinic.getId()).append(": ").append(clinic.getName()).append("\n");
                }
                response.setReply(response.getReply() + sb);
                response.setClinics(clinics);
            }
            return response;
        }

        List<SlotInfo> slots = getAvailableSlots(clinicId, date);

        if (slots.isEmpty()) {
            response.setReply("Aucun créneau disponible pour le " + date + ". Veuillez essayer une autre date.");
        } else {
            StringBuilder sb = new StringBuilder("Voici les créneaux disponibles pour le **")
                    .append(date).append("**:\n\n");
            for (SlotInfo slot : slots) {
                sb.append("🕐 ").append(slot.getStartTime()).append(" - ").append(slot.getEndTime()).append("\n");
            }
            sb.append("\nPour prendre rendez-vous, veuillez contacter le cabinet directement.");
            response.setReply(sb.toString());
            response.setAvailableSlots(slots);
            response.setSelectedClinicId(clinicId);
        }

        return response;
    }

    private ChatMessageResponse handleGreeting(ChatMessageResponse response) {
        response.setReply("Bonjour ! 👋 Bienvenue sur notre assistant virtuel.\n\n" +
                "Je peux vous aider avec:\n" +
                "- 🏥 Informations sur nos cabinets\n" +
                "- 👨‍⚕️ Liste des médecins disponibles\n" +
                "- 📅 Créneaux horaires disponibles\n\n" +
                "Comment puis-je vous aider aujourd'hui ?");
        return response;
    }

    private ChatMessageResponse handleHelp(ChatMessageResponse response) {
        response.setReply("Je suis votre assistant virtuel. Voici ce que je peux faire pour vous:\n\n" +
                "📍 **Informations sur les cabinets**: Dites 'cabinets' ou 'cliniques'\n" +
                "👨‍⚕️ **Médecins d'un cabinet**: Dites 'médecins du cabinet X'\n" +
                "📅 **Créneaux disponibles**: Dites 'créneaux disponibles pour le cabinet X'\n\n" +
                "N'hésitez pas à me poser vos questions !");
        return response;
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
