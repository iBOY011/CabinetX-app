package com.gi.appointmentservice.Model.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import com.gi.appointmentservice.Model.Enum.MotifRDV;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de base pour la mise à jour d'un rendez-vous.
 * 
 * <p>Contient les champs modifiables d'un rendez-vous existant :
 * <ul>
 *   <li>date : date du rendez-vous</li>
 *   <li>Heure_debut : heure de début</li>
 *   <li>Heure_fin : heure de fin</li>
 *   <li>motifRDV : motif (CONSULTATION, CONTROL)</li>
 *   <li>notes : notes complémentaires (max 500 caractères)</li>
 * </ul>
 * 
 * <p>Classe parente de RDVRequest pour réutiliser les validations communes.
 * Utilisée directement pour les requêtes de mise à jour (PUT /api/appointments/update/{id}).
 * 
 * <p>Validations appliquées :
 * <ul>
 *   <li>Tous les champs sauf notes sont obligatoires</li>
 *   <li>Notes limitées à 500 caractères</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateDto {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;
    
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime Heure_debut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime Heure_fin;
    
    @NotNull(message = "Le motif du rendez-vous est obligatoire")
    private MotifRDV motifRDV;
    
    @Size(max = 500, message = "Les notes sont trop longues (max 500 caractères)")
    private String notes;
}
