package com.gi.patientservice.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe embarquée représentant une adresse postale marocaine.
 * 
 * <p>Cette classe est utilisée comme composant embarqué (@Embeddable) dans l'entité Patient.
 * Les champs d'adresse sont stockés directement dans la table patient sans créer
 * de table séparée.
 * 
 * <p>Supporte les adresses marocaines avec :
 * <ul>
 *   <li>Rue/quartier</li>
 *   <li>Ville marocaine</li>
 *   <li>Code postal (format marocain à 5 chiffres)</li>
 *   <li>Pays (principalement "Maroc")</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Adresse {
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
}
