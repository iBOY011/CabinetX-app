package com.gi.appointmentservice.Model.Enum;

/**
 * Énumération des motifs de rendez-vous médicaux.
 * 
 * <p>Définit les types de consultations possibles dans le système CabinetX.
 * Utilisé pour catégoriser les rendez-vous et générer des statistiques.
 * 
 * <p>Peut être étendu dans le futur pour inclure d'autres types de consultations
 * (urgence, vaccination, bilan de santé, etc.).
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public enum MotifRDV {
    /** Consultation médicale standard (première visite ou nouvelle pathologie) */
    CONSULTATION, 
    
    /** Consultation de contrôle (suivi d'un traitement ou d'une pathologie existante) */
    CONTROL,
}
