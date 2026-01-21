package com.gi.appointmentservice.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.gi.appointmentservice.Mapper.RDVMapper;
import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;
import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.DTO.UpdateDto;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;
import com.gi.appointmentservice.Repository.RDVRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service métier pour la gestion du cycle de vie des rendez-vous (RDV).
 * 
 * <p>Gère l'ensemble des opérations liées aux rendez-vous médicaux :
 * <ul>
 *   <li>Création avec validation des règles métier</li>
 *   <li>Modification des informations (date, heure, motif)</li>
 *   <li>Gestion des statuts (CONFIRMÉ, EN_CONSULTATION, TERMINÉ, ANNULÉ)</li>
 *   <li>Suppression de rendez-vous</li>
 *   <li>Consultation par patient et par date</li>
 *   <li>Recherche par cabinet et date</li>
 * </ul>
 * 
 * <p>Règles métier appliquées :
 * <ul>
 *   <li>Heure début doit être avant heure fin</li>
 *   <li>Pas de rendez-vous dans le passé</li>
 *   <li>Un patient ne peut avoir qu'un seul rendez-vous actif par jour</li>
 *   <li>Transitions de statut contrôlées (pas de changement direct CONFIRMÉ → TERMINÉ)</li>
 *   <li>Statuts terminaux (ANNULÉ, TERMINÉ, MISSING) non modifiables</li>
 * </ul>
 * 
 * <p>Intégrations :
 * <ul>
 *   <li>PatientClient : Récupération des informations patient</li>
 *   <li>Kafka : Réception de messages via welcomeConsumer</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Service
@RequiredArgsConstructor

public class RDVService {
    private final RDVRepository rdvRepository;
    private final RDVMapper RDVMapper;
    private final PatientClient patientClient;
    private PatientInfoDTO patientInfo;

    /**
     * Crée un nouveau rendez-vous avec validation complète des règles métier.
     * 
     * <p>Validations appliquées :
     * <ol>
     *   <li>Heure de début strictement antérieure à l'heure de fin</li>
     *   <li>Interdiction des rendez-vous dans le passé (date ou heure)</li>
     *   <li>Vérification de l'unicité : un patient ne peut avoir qu'un seul RDV actif par jour</li>
     * </ol>
     * 
     * <p>Après création, enrichit la réponse avec les informations complètes du patient
     * en interrogeant le microservice Patient.
     * 
     * @param rdvreqDto les informations du rendez-vous à créer
     * @return RDVResponse contenant le rendez-vous créé avec les infos patient
     * @throws IllegalArgumentException si validation échouée ou doublon détecté
     */
    public RDVResponse createRendezVous(RDVRequest rdvreqDto) {

        // 1. date début < date fin
        if (!rdvreqDto.getHeure_debut().isBefore(rdvreqDto.getHeure_fin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin.");
        }
        
        // 2. Ne pas autoriser les rendez-vous dans le passé
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        if (rdvreqDto.getDate().isBefore(today)) {
            throw new IllegalArgumentException("Impossible de prendre un rendez-vous à une date passée.");
        }
        
        if (rdvreqDto.getDate().isEqual(today) && rdvreqDto.getHeure_debut().isBefore(now)) {
            throw new IllegalArgumentException("Impossible de prendre un rendez-vous à une heure passée.");
        }

        // 3. Un même patient ne peut pas avoir deux rendez-vous actifs le même jour
        List<RendezVous> existingAppointments = rdvRepository.findActiveAppointmentsByPatientAndDate(
            rdvreqDto.getPatientId(), 
            rdvreqDto.getDate()
        );
        
        if (!existingAppointments.isEmpty()) {
            throw new IllegalArgumentException("Ce patient a déjà un rendez-vous programmé pour cette date.");
        }

        // faut regle metier
        RendezVous rdv = new RendezVous();
        rdv = RDVMapper.toEntity(rdvreqDto);
        rdvRepository.save(rdv);
        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo);

    }

    /**
     * Met à jour les informations d'un rendez-vous existant.
     * 
     * <p>Permet de modifier :
     * <ul>
     *   <li>Date et horaires (début/fin)</li>
     *   <li>Motif du rendez-vous</li>
     *   <li>Notes complémentaires</li>
     * </ul>
     * 
     * <p>Note : Le statut doit être modifié via updateStatusRendezVous() pour 
     * garantir les transitions valides.
     * 
     * @param rdvId identifiant du rendez-vous à modifier
     * @param updateDto nouvelles informations du rendez-vous
     * @return RDVResponse contenant le rendez-vous mis à jour
     * @throws ResourceNotFoundException si le rendez-vous n'existe pas
     */
    public RDVResponse updateRendezVous(Long rdvId, UpdateDto updateDto) {
        RendezVous rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous not found with id: " + rdvId));
        rdv.setDate(updateDto.getDate());
        rdv.setHeure_debut(updateDto.getHeure_debut());
        rdv.setHeure_fin(updateDto.getHeure_fin());
        rdv.setMotifRDV(updateDto.getMotifRDV());
        rdv.setNotes(updateDto.getNotes());
        rdvRepository.save(rdv);

        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo);
    }

    public void deleteRendezVous(Long rdvId) {
        rdvRepository.deleteById(rdvId);
    }

    public RDVResponse getRendezVousById(Long rdvId) {
        RendezVous rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous not found with id: " + rdvId));
        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo); // here we should retrieve actual patient names from Patient
                                                       // Service
    }

    public RDVResponse getTodayRendezVousForPatient(Long patientId) {
        List<StatutRDV> validStatuts = Arrays.asList(StatutRDV.CONFIRME, StatutRDV.EN_CONSULTATION);
        RendezVous rdv = rdvRepository.findFirstValidToday(patientId, LocalDate.now(), validStatuts)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun rendez-vous valide aujourd'hui pour le patient: " + patientId));

        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo);
    }

    @Bean
    public Consumer<String> welcomeConsumer() {
        return (message) -> {
            System.out.println("************************************************");
            System.out.println("************************************************");
            System.out.println("Received message from Kafka: " + message);
            System.out.println("************************************************");
            System.out.println("************************************************");
        };
    }

    /**
     * Met à jour le statut d'un rendez-vous avec validation des transitions.
     * 
     * <p>Règles de transition de statut :
     * <ul>
     *   <li>Statuts terminaux (ANNULÉ, TERMINÉ, MISSING) : non modifiables</li>
     *   <li>Depuis EN_CONSULTATION : transition uniquement vers TERMINÉ</li>
     *   <li>Depuis CONFIRMÉ : pas de transition directe vers TERMINÉ (doit passer par EN_CONSULTATION)</li>
     * </ul>
     * 
     * <p>Ces règles garantissent un workflow cohérent et traçable du cycle de vie
     * du rendez-vous.
     * 
     * @param id identifiant du rendez-vous
     * @param statut nouveau statut à appliquer
     * @return RDVResponse contenant le rendez-vous avec statut mis à jour
     * @throws ResourceNotFoundException si le rendez-vous n'existe pas
     * @throws IllegalArgumentException si la transition de statut est invalide
     */
    public RDVResponse updateStatusRendezVous(Long id, StatutRDV statut) {
        RendezVous rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous not found with id: " + id));
        if (rdv.getStatutRDV() == StatutRDV.ANNULE || rdv.getStatutRDV() == StatutRDV.TERMINE
                || rdv.getStatutRDV() == StatutRDV.MISSING) {
            throw new IllegalArgumentException("Le statut ne peut pas être modifié.");
        }

        if (rdv.getStatutRDV() == StatutRDV.EN_CONSULTATION && statut != StatutRDV.TERMINE) {
            throw new IllegalArgumentException("Le statut ne peut être changé que vers TERMINE .");
        }
        if (rdv.getStatutRDV() == StatutRDV.CONFIRME && statut == StatutRDV.TERMINE) {
            throw new IllegalArgumentException("Le statut ne peut pas être changé directement de CONFIRME à TERMINE.");
        }
        rdv.setStatutRDV(statut);

        rdvRepository.save(rdv);
        PatientInfoDTO patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo);
    }

    public List<RDVResponse> getAppointmentsByDate(LocalDate date, Long cabinetId) {
        List<RendezVous> rendezVousList = rdvRepository.findByDateAndCabinetId(date, cabinetId);
        return rendezVousList.stream()
                .map(rdv -> {
                    PatientInfoDTO patientInfo = patientClient.getPatientById(rdv.getPatientId());
                    return RDVMapper.toResponse(rdv, patientInfo);
                })
                .collect(java.util.stream.Collectors.toList());
    }

}
