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

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RDVRepository rdvRepository;
    private final RDVMapper rdvMapper;
    private final PatientClient patientClient;

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
            if (a.getQueuePosition() == null) return 1;
            if (b.getQueuePosition() == null) return -1;
            return a.getQueuePosition().compareTo(b.getQueuePosition());
        });

        return queueAppointments.stream()
                .map(rdv -> {
                    var patientInfo = patientClient.getPatientById(rdv.getPatientId());
                    return rdvMapper.toResponse(rdv, patientInfo);
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
        return rdvMapper.toResponse(saved, patientInfo);
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
        return rdvMapper.toResponse(saved, patientInfo);
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
        return rdvMapper.toResponse(saved, patientInfo);
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
