package com.gi.appointmentservice.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gi.appointmentservice.Mapper.RDVMapper;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Repository.RDVRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service métier pour la gestion de la file d'attente des rendez-vous.
 * 
 * <p>Gère le système de file d'attente permettant d'organiser le flux de patients
 * en salle d'attente. Fonctionnalités principales :
 * <ul>
 *   <li>Ajout automatique de rendez-vous dans la file d'attente</li>
 *   <li>Gestion des positions dans la file (queuePosition)</li>
 *   <li>Réordonnancement manuel de la file</li>
 *   <li>Appel du prochain patient</li>
 *   <li>Retrait de la file d'attente</li>
 *   <li>Notifications automatiques (SMS, push) aux patients</li>
 * </ul>
 * 
 * <p>Workflow typique :
 * <ol>
 *   <li>Patient arrive au cabinet → addToQueue()</li>
 *   <li>File d'attente affichée en salle d'attente → getQueueByDateAndCabinet()</li>
 *   <li>Secrétaire/Médecin appelle le patient suivant → callNextPatient()</li>
 *   <li>Patient entre en consultation → removeFromQueue()</li>
 * </ol>
 * 
 * <p>Intégrations :
 * <ul>
 *   <li>PatientClient : Récupération des informations patient</li>
 *   <li>NotificationClient : Envoi de notifications lors des changements de position</li>
 *   <li>UserClient : Récupération des informations utilisateur (médecin)</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Service
@RequiredArgsConstructor
public class QueueService {

    private final RDVRepository rdvRepository;
    private final PatientClient patientClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    /**
     * Get all appointments in queue for a specific date and cabinet
     * Ordered by queuePosition (then by appointment time)
     */
    public List<RDVResponse> getQueueByDateAndCabinet(LocalDate date, Long cabinetId) {
        List<RendezVous> queueAppointments = rdvRepository
                .findByCabinetIdAndDateAndStatutRDV(cabinetId, date, StatutRDV.EN_ATTENTE);

        // Sort by queuePosition (nulls last), then by Heure_debut
        queueAppointments.sort((a, b) -> {
            if (a.getQueuePosition() == null && b.getQueuePosition() == null) {
                return a.getHeure_debut().compareTo(b.getHeure_debut());
            }
            if (a.getQueuePosition() == null)
                return 1;
            if (b.getQueuePosition() == null)
                return -1;
            return a.getQueuePosition().compareTo(b.getQueuePosition());
        });

        return queueAppointments.stream()
                .map(rdv -> {
                    var patientInfo = patientClient.getPatientById(rdv.getPatientId());
                    return RDVMapper.toResponse(rdv, patientInfo);
                })
                .collect(Collectors.toList());
    }

    /**
     * Add an appointment to the queue (change status to EN_ATTENTE)
     * Automatically assigns queue position
     */
    @Transactional
    public RDVResponse addToQueue(Long appointmentId) {
        RendezVous rdv = rdvRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        // Check if already in queue
        if (rdv.getStatutRDV() == StatutRDV.EN_ATTENTE) {
            throw new RuntimeException("Appointment is already in queue");
        }

        // Get current max queue position for this cabinet/date
        Integer maxPosition = rdvRepository.findMaxQueuePositionByCabinetAndDate(
                rdv.getCabinetId(), rdv.getDate());

        // Assign next position
        rdv.setQueuePosition(maxPosition == null ? 1 : maxPosition + 1);
        rdv.setStatutRDV(StatutRDV.EN_ATTENTE);

        RendezVous saved = rdvRepository.save(rdv);
        var patientInfo = patientClient.getPatientById(saved.getPatientId());
        return RDVMapper.toResponse(saved, patientInfo);
    }

    /**
     * Remove appointment from queue (reset status to CONFIRME)
     */
    @Transactional
    public RDVResponse removeFromQueue(Long appointmentId) {
        RendezVous rdv = rdvRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        if (rdv.getStatutRDV() != StatutRDV.EN_ATTENTE) {
            throw new RuntimeException("Appointment is not in queue");
        }

        Integer removedPosition = rdv.getQueuePosition();
        rdv.setStatutRDV(StatutRDV.CONFIRME);
        rdv.setQueuePosition(null);

        RendezVous saved = rdvRepository.save(rdv);

        // Reorder remaining queue items
        if (removedPosition != null) {
            reorderQueueAfterRemoval(rdv.getCabinetId(), rdv.getDate(), removedPosition);
        }

        var patientInfo = patientClient.getPatientById(saved.getPatientId());
        return RDVMapper.toResponse(saved, patientInfo);
    }

    /**
     * Call next patient (move to EN_CONSULTATION)
     */
    @Transactional
    public RDVResponse callNext(Long appointmentId) {
        RendezVous rdv = rdvRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        if (rdv.getStatutRDV() != StatutRDV.EN_ATTENTE) {
            throw new RuntimeException("Appointment is not in queue");
        }

        Integer removedPosition = rdv.getQueuePosition();
        rdv.setStatutRDV(StatutRDV.EN_CONSULTATION);
        rdv.setQueuePosition(null);

        RendezVous saved = rdvRepository.save(rdv);

        // Reorder remaining queue items
        if (removedPosition != null) {
            reorderQueueAfterRemoval(rdv.getCabinetId(), rdv.getDate(), removedPosition);
        }

        var patientInfo = patientClient.getPatientById(saved.getPatientId());
        
        System.out.println("[QueueService] callNext - appointmentId: " + saved.getId() + ", cabinetId: " + saved.getCabinetId());

        // Get doctor ID from user service by clinic ID
        Long doctorId = null;
        try {
            System.out.println("[QueueService] Fetching users for clinicId: " + saved.getCabinetId());
            var users = userClient.getUsersByClinic(saved.getCabinetId());
            
            // Find the first MEDCIN role user
            if (users != null && !users.isEmpty()) {
                var doctor = users.stream()
                        .filter(user -> "MEDCIN".equalsIgnoreCase(user.getRole()))
                        .findFirst()
                        .orElse(null);
                
                if (doctor != null) {
                    doctorId = doctor.getId();
                    System.out.println("[QueueService] Retrieved doctorId: " + doctorId + " (Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + ") from user service");
                } else {
                    System.err.println("[QueueService] No doctor (MEDCIN) found for clinic " + saved.getCabinetId());
                }
            } else {
                System.err.println("[QueueService] No users found for clinic " + saved.getCabinetId());
            }
        } catch (Exception e) {
            System.err.println("[QueueService] Failed to retrieve doctor from user service: " + e.getMessage());
            e.printStackTrace();
        }

        // Send real-time notification to doctor
        System.out.println("[QueueService] Checking notification conditions - doctorId: " + doctorId);
        if (doctorId != null && doctorId > 0) {
            try {
                Integer patientAge = calculateAge(patientInfo.getDateNaissance());
                System.out.println("[QueueService] Sending notification to doctorId: " + doctorId + " for patient: " + patientInfo.getPrenom() + " " + patientInfo.getNom());
                notificationClient.sendPatientConsultationNotification(
                        doctorId,
                        saved.getId(),
                        saved.getPatientId(),
                        patientInfo.getPrenom() + " " + patientInfo.getNom(),
                        patientAge,
                        saved.getMotifRDV().toString(),
                        saved.getHeure_debut().toString());
                System.out.println("[QueueService] Notification sent successfully");
            } catch (Exception e) {
                System.err.println("[QueueService] Failed to send notification: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("[QueueService] No valid doctor ID found (doctorId=" + doctorId + "), skipping notification");
        }

        return RDVMapper.toResponse(saved, patientInfo);
    }

    /**
     * Calculate patient age from birth date
     */
    private Integer calculateAge(java.time.LocalDate dateNaissance) {
        if (dateNaissance == null)
            return 0;
        return java.time.Period.between(dateNaissance, java.time.LocalDate.now()).getYears();
    }

    /**
     * Reorder queue positions (for drag & drop)
     */
    @Transactional
    public List<RDVResponse> reorderQueue(Long cabinetId, LocalDate date, List<Long> appointmentIds) {
        // Validate all appointments exist and are in queue
        List<RendezVous> appointments = rdvRepository.findAllById(appointmentIds);

        if (appointments.size() != appointmentIds.size()) {
            throw new RuntimeException("Some appointments not found");
        }

        // Update positions based on order in the list
        for (int i = 0; i < appointmentIds.size(); i++) {
            Long appointmentId = appointmentIds.get(i);
            RendezVous rdv = appointments.stream()
                    .filter(a -> a.getId().equals(appointmentId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            if (rdv.getStatutRDV() != StatutRDV.EN_ATTENTE) {
                throw new RuntimeException("Appointment " + appointmentId + " is not in queue");
            }

            rdv.setQueuePosition(i + 1);
        }

        rdvRepository.saveAll(appointments);

        // Return updated queue
        return getQueueByDateAndCabinet(date, cabinetId);
    }

    /**
     * Helper method to reorder queue after removing an item
     */
    private void reorderQueueAfterRemoval(Long cabinetId, LocalDate date, Integer removedPosition) {
        List<RendezVous> queueAppointments = rdvRepository
                .findByCabinetIdAndDateAndStatutRDV(cabinetId, date, StatutRDV.EN_ATTENTE);

        // Decrement positions for items after the removed position
        queueAppointments.stream()
                .filter(rdv -> rdv.getQueuePosition() != null && rdv.getQueuePosition() > removedPosition)
                .forEach(rdv -> rdv.setQueuePosition(rdv.getQueuePosition() - 1));

        rdvRepository.saveAll(queueAppointments);
    }
}
