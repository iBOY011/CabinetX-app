package com.gi.appointmentservice.Repository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;

/**
 * Repository pour la gestion de la persistence des rendez-vous.
 * 
 * <p>Fournit des méthodes de requêtes spécialisées pour :
 * <ul>
 *   <li>Recherche des rendez-vous par date et cabinet</li>
 *   <li>Validation de l'unicité (un patient/un RDV actif par jour)</li>
 *   <li>Gestion de la file d'attente (positions, statut EN_ATTENTE)</li>
 *   <li>Recherche du premier rendez-vous valide du jour pour un patient</li>
 * </ul>
 * 
 * <p>Toutes les requêtes liées à la file d'attente filtrent sur le statut EN_ATTENTE
 * et utilisent queuePosition pour l'ordre.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Repository
public interface RDVRepository extends JpaRepository<RendezVous, Long> {
	/**
	 * Trouve le premier rendez-vous valide du jour pour un patient.
	 * 
	 * <p>Recherche parmi les statuts spécifiés (généralement CONFIRMÉ, EN_CONSULTATION)
	 * et retourne le RDV avec l'heure de début la plus proche.
	 * 
	 * @param patientId identifiant du patient
	 * @param date date du rendez-vous
	 * @param statuts collection de statuts valides à considérer
	 * @return Optional contenant le premier RDV si trouvé
	 */
	@Query("select r from RendezVous r where r.patientId = :patientId and r.date = :date and r.statutRDV in :statuts order by r.Heure_debut asc")
	Optional<RendezVous> findFirstValidToday(@Param("patientId") Long patientId, @Param("date") LocalDate date, @Param("statuts") Collection<StatutRDV> statuts);

    /**
     * Récupère tous les rendez-vous d'un cabinet pour une date donnée.
     * 
     * @param date date des rendez-vous
     * @param cabinetId identifiant du cabinet
     * @return liste de tous les rendez-vous (tous statuts)
     */
    List<RendezVous> findByDateAndCabinetId(LocalDate date, Long cabinetId);

    /**
     * Trouve les rendez-vous actifs (non annulés) pour un patient à une date.
     * 
     * <p>Utilisé pour valider qu'un patient n'a pas déjà un RDV actif avant d'en créer un nouveau.
     * 
     * @param patientId identifiant du patient
     * @param date date des rendez-vous
     * @return liste des rendez-vous actifs (excluant ANNULÉ)
     */
    @Query("SELECT r FROM RendezVous r WHERE r.patientId = :patientId AND r.date = :date " +
            "AND r.statutRDV NOT IN (com.gi.appointmentservice.Model.Enum.StatutRDV.ANNULE)")
    List<RendezVous> findActiveAppointmentsByPatientAndDate(@Param("patientId") Long patientId,
                                                             @Param("date") LocalDate date);

    /**
     * Récupère les rendez-vous d'un cabinet pour une date et un statut donnés.
     * 
     * <p>Principalement utilisé pour obtenir la file d'attente (statut EN_ATTENTE).
     * 
     * @param cabinetId identifiant du cabinet
     * @param date date des rendez-vous
     * @param statutRDV statut à filtrer
     * @return liste des rendez-vous correspondants
     */
    List<RendezVous> findByCabinetIdAndDateAndStatutRDV(Long cabinetId, LocalDate date, StatutRDV statutRDV);

    /**
     * Trouve la position maximale dans la file d'attente pour un cabinet et une date.
     * 
     * <p>Utilisé pour assigner la prochaine position lors de l'ajout d'un patient dans la file.
     * 
     * @param cabinetId identifiant du cabinet
     * @param date date concernée
     * @return position maximale, ou null si la file est vide
     */
    @Query("SELECT MAX(r.queuePosition) FROM RendezVous r WHERE r.cabinetId = :cabinetId AND r.date = :date AND r.statutRDV = 'EN_ATTENTE'")
    Integer findMaxQueuePositionByCabinetAndDate(@Param("cabinetId") Long cabinetId, @Param("date") LocalDate date);

}
