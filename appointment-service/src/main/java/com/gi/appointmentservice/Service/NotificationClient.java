package com.gi.appointmentservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client HTTP pour la communication avec le microservice Notification.
 * 
 * <p>Gère l'envoi de notifications aux utilisateurs (médecins, patients) lors
 * d'événements liés aux rendez-vous et à la file d'attente.
 * 
 * <p>Types de notifications envoyées :
 * <ul>
 *   <li>Notification médecin : Patient appelé en consultation</li>
 *   <li>Notification patient : Changement de position dans la file</li>
 *   <li>Rappel rendez-vous : 24h et 1h avant le RDV</li>
 *   <li>Confirmation : Rendez-vous créé/modifié</li>
 * </ul>
 * 
 * <p>Canaux de notification supportés (côté Notification Service) :
 * <ul>
 *   <li>SMS : Via Twilio pour patients marocains</li>
 *   <li>Push : Notifications mobiles via Firebase</li>
 *   <li>Email : Pour confirmations et rappels</li>
 * </ul>
 * 
 * <p>Pattern architectural :
 * <ul>
 *   <li>Fire-and-forget : Appels asynchrones non bloquants</li>
 *   <li>Gestion d'erreurs : Logs détaillés, pas de propagation d'exception</li>
 *   <li>Service Discovery : Résolution via Eureka</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Service
public class NotificationClient {

    private final WebClient webClient;

    public NotificationClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Envoie une notification au médecin lorsqu'un patient est appelé en consultation.
     * 
     * <p>Déclenchée par QueueService.callNext() lorsque le patient passe de EN_ATTENTE
     * à EN_CONSULTATION. Le médecin reçoit une notification push et/ou SMS avec :
     * <ul>
     *   <li>Nom du patient</li>
     *   <li>Âge du patient</li>
     *   <li>Motif du rendez-vous</li>
     *   <li>Heure du rendez-vous</li>
     * </ul>
     * 
     * <p>Méthode non bloquante : les erreurs sont loggées mais ne font pas échouer
     * l'appel du patient (la consultation peut continuer même si notification échoue).
     * 
     * @param doctorId identifiant du médecin destinataire
     * @param appointmentId identifiant du rendez-vous
     * @param patientId identifiant du patient
     * @param patientName nom complet du patient
     * @param patientAge âge du patient en années
     * @param reason motif de consultation (CONSULTATION, CONTROL)
     * @param appointmentTime heure du rendez-vous au format HH:mm
     */
    public void sendPatientConsultationNotification(
            Long doctorId,
            Long appointmentId,
            Long patientId,
            String patientName,
            Integer patientAge,
            String reason,
            String appointmentTime) {

        System.out.println("[NotificationClient] Starting notification send - doctorId: " + doctorId + ", patient: " + patientName);
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("NOTIFICATION-SERVICE")
                            .path("/api/notifications/patient-consultation")
                            .queryParam("doctorId", doctorId)
                            .queryParam("appointmentId", appointmentId)
                            .queryParam("patientId", patientId)
                            .queryParam("patientName", patientName)
                            .queryParam("patientAge", patientAge)
                            .queryParam("reason", reason)
                            .queryParam("appointmentTime", appointmentTime)
                            .build())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(result -> System.out.println("[NotificationClient] HTTP call completed successfully for doctorId: " + doctorId))
                    .doOnError(error -> System.err.println("[NotificationClient] HTTP call failed: " + error.getMessage()))
                    .subscribe(); // Non-blocking call

            System.out.println("[NotificationClient] Notification HTTP request initiated for doctor ID: " + doctorId + " for patient: " + patientName);
        } catch (Exception e) {
            System.err.println("[NotificationClient] Exception during notification send: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - notification failure shouldn't block the main operation
        }
    }
}
