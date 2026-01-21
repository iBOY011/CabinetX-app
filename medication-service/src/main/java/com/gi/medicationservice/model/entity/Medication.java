package com.gi.medicationservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité JPA représentant un médicament du catalogue.
 * 
 * <p>Contient les informations essentielles pour identifier et prescrire un médicament.
 * Utilisé dans Prescription Service pour créer des ordonnances.</p>
 * 
 * <p><b>Champs clés :</b></p>
 * <ul>
 *   <li><b>id :</b> Identifiant unique généré automatiquement</li>
 *   <li><b>commercialName :</b> Nom commercial (ex: "Doliprane", "Amoxicilline Mylan")</li>
 *   <li><b>dci :</b> Dénomination Commune Internationale (ex: "Paracétamol", "Amoxicilline")</li>
 *   <li><b>dosage :</b> Dosage unitaire (ex: "500mg", "1g", "250mg/5ml")</li>
 *   <li><b>form :</b> Forme galénique (ex: "Comprimé", "Gélule", "Sirop", "Injectable")</li>
 * </ul>
 * 
 * <p><b>Indexation recherche :</b></p>
 * <ul>
 *   <li>commercialName : Index partiel pour autocomplete (LIKE %term%)</li>
 *   <li>dci : Index partiel pour recherche par principe actif</li>
 * </ul>
 * 
 * <p><b>Exemple :</b></p>
 * <pre>
 * Medication med = new Medication();
 * med.setCommercialName("Doliprane");
 * med.setDci("Paracétamol");
 * med.setDosage("1000mg");
 * med.setForm("Comprimé effervescent");
 * </pre>
 * 
 * <p><b>Table PostgreSQL :</b> medication</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commercialName;

    private String dci;

    private String dosage;

    private String form;
}