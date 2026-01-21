package com.gi.patientservice.enums;

/**
 * Énumération des types d'événements dans le cycle de vie d'un patient.
 * 
 * <p>Utilisé pour l'audit trail et le tracking des modifications dans PatientEvent.
 * Permet de reconstruire l'historique complet des changements pour chaque patient.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public enum EventType {
    /** Événement de création d'un nouveau patient */
    CREATED,
    
    /** Événement de mise à jour des informations patient */
    UPDATED,
    
    /** Événement de suppression d'un patient */
    DELETED
}
