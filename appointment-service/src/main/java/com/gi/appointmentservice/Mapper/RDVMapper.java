package com.gi.appointmentservice.Mapper;

import org.springframework.stereotype.Component;

import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;
import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;

/**
 * Mapper pour la conversion entre les entités RendezVous et leurs DTOs.
 * 
 * <p>Gère deux types de transformations :
 * <ul>
 *   <li>RDVRequest → RendezVous : création d'une nouvelle entité avec statut CONFIRMÉ par défaut</li>
 *   <li>RendezVous + PatientInfoDTO → RDVResponse : enrichissement avec données patient</li>
 * </ul>
 * 
 * <p>Le mapper ne gère pas l'ID lors de la création (assigné par la base de données).
 * La position dans la file (queuePosition) est gérée par QueueService et non par le mapper.
 * 
 * <p>Pattern architectural :
 * <ul>
 *   <li>Séparation des couches : entités JPA vs DTOs d'API</li>
 *   <li>Enrichissement via microservices : intégration des données patient</li>
 *   <li>Statut par défaut : CONFIRMÉ à la création</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Component
public class RDVMapper {

    /**
     * Convertit un RDVRequest en entité RendezVous pour création.
     * 
     * <p>Initialise le statut à CONFIRMÉ par défaut. L'ID et queuePosition
     * ne sont pas assignés (null) et seront gérés ultérieurement.
     * 
     * @param dto le DTO de requête contenant les informations du rendez-vous
     * @return une nouvelle entité RendezVous avec statut CONFIRMÉ
     */
    // Request → Entity (when creating RDV)
    public static RendezVous toEntity(RDVRequest dto) {
        RendezVous rdv = new RendezVous();
        rdv.setPatientId(dto.getPatientId());
        rdv.setCabinetId(dto.getCabinetId());
        rdv.setDate(dto.getDate());
        rdv.setHeure_debut(dto.getHeure_debut());
        rdv.setHeure_fin(dto.getHeure_fin());
        rdv.setMotifRDV(dto.getMotifRDV());
        rdv.setNotes(dto.getNotes());
        rdv.setStatutRDV(StatutRDV.CONFIRME);
        
        return rdv;
    }

    /**
     * Convertit une entité RendezVous en RDVResponse enrichi avec les infos patient.
     * 
     * <p>Fusionne les données du rendez-vous avec les informations complètes du patient
     * (nom, prénom, CIN, date de naissance) obtenues depuis PatientClient.
     * 
     * <p>Cette approche évite les jointures inter-services et respecte l'architecture
     * microservices en isolant les domaines.
     * 
     * @param rdv l'entité RendezVous source
     * @param patientInfo les informations patient du microservice Patient
     * @return RDVResponse complet avec données rendez-vous + patient
     */
    // Entity + patient names → Response
    public static RDVResponse toResponse(RendezVous rdv, PatientInfoDTO patientInfo) {
        RDVResponse dto = new RDVResponse();
        dto.setId(rdv.getId());
        dto.setPatientId(rdv.getPatientId());
        dto.setPrenom(patientInfo.getPrenom());
        dto.setNom(patientInfo.getNom());
        dto.setCin(patientInfo.getCin());
        dto.setDateNaissance(patientInfo.getDateNaissance());
        dto.setCabinetId(rdv.getCabinetId());
        dto.setDate(rdv.getDate());
        dto.setHeure_debut(rdv.getHeure_debut());
        dto.setHeure_fin(rdv.getHeure_fin());
        dto.setMotifRDV(rdv.getMotifRDV());
        dto.setNotes(rdv.getNotes());
        dto.setStatutRDV(rdv.getStatutRDV());
        dto.setQueuePosition(rdv.getQueuePosition());
        return dto;
    }
}
