package com.gi.patientservice.enums;

/**
 * Énumération des types de couverture santé au Maroc.
 * 
 * <p>Représente les principaux organismes de couverture médicale marocains :
 * <ul>
 *   <li>CNSS - Caisse Nationale de Sécurité Sociale (secteur privé)</li>
 *   <li>CNOPS - Caisse Nationale des Organismes de Prévoyance Sociale (fonction publique)</li>
 *   <li>PRIVÉE - Assurances privées complémentaires</li>
 *   <li>AUCUNE - Patient sans couverture médicale</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
public enum TypeMutuelle {
    /** Aucune couverture médicale */
    AUCUNE,
    
    /** Caisse Nationale de Sécurité Sociale (secteur privé) */
    CNSS,
    
    /** Caisse Nationale des Organismes de Prévoyance Sociale (fonction publique) */
    CNOPS,
    
    /** Assurance santé privée */
    PRIVEE
}
