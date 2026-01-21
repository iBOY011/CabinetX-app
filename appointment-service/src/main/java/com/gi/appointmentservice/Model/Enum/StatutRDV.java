package com.gi.appointmentservice.Model.Enum;

/**
 * Énumération des statuts possibles d'un rendez-vous médical.
 * 
 * <p>Représente le cycle de vie complet d'un rendez-vous avec les transitions suivantes :
 * <pre>
 * CONFIRME → EN_ATTENTE → EN_CONSULTATION → TERMINE
 *     ↓          ↓              ↓
 *  ANNULE     ANNULE        MISSING
 * </pre>
 * 
 * <p>Règles de transition (appliquées par RDVService.updateStatusRendezVous) :
 * <ul>
 *   <li>CONFIRMÉ : rendez-vous créé, patient pas encore arrivé</li>
 *   <li>EN_ATTENTE : patient arrivé, dans la file d'attente</li>
 *   <li>EN_CONSULTATION : patient appelé, consultation en cours</li>
 *   <li>TERMINÉ : consultation terminée (statut final)</li>
 *   <li>ANNULÉ : rendez-vous annulé (statut final)</li>
 *   <li>MISSING : patient absent, n'est pas venu (statut final)</li>
 * </ul>
 * 
 * <p>Les statuts finaux (TERMINÉ, ANNULÉ, MISSING) ne peuvent plus être modifiés.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public enum StatutRDV {
    /** Rendez-vous confirmé, patient pas encore arrivé */
    CONFIRME,
    
    /** Patient arrivé, en attente dans la file */
    EN_ATTENTE,
    
    /** Patient appelé, consultation en cours */
    EN_CONSULTATION,
    
    /** Rendez-vous annulé (statut final) */
    ANNULE,
    
    /** Consultation terminée (statut final) */
    TERMINE, 
    
    /** Patient absent, non présenté (statut final) */
    MISSING
    
}
