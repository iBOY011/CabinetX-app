package com.gi.appointmentservice.Model.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un nouveau rendez-vous.
 * 
 * <p>Hérite de UpdateDto pour réutiliser les champs communs (date, heures, motif, notes)
 * et ajoute les identifiants obligatoires pour la création :
 * <ul>
 *   <li>patientId : identifiant du patient</li>
 *   <li>cabinetId : identifiant du cabinet médical</li>
 * </ul>
 * 
 * <p>Toutes les validations sont appliquées via les annotations Jakarta Validation.
 * 
 * <p>Exemple d'utilisation :
 * <pre>
 * RDVRequest request = new RDVRequest();
 * request.setPatientId(123L);
 * request.setCabinetId(1L);
 * request.setDate(LocalDate.of(2024, 6, 15));
 * request.setHeure_debut(LocalTime.of(14, 30));
 * request.setHeure_fin(LocalTime.of(15, 0));
 * request.setMotifRDV(MotifRDV.CONSULTATION);
 * </pre>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RDVRequest extends UpdateDto{

    @NotNull(message = "Le patient est obligatoire")
    @Positive(message = "ID du patient invalide")
    private Long patientId;
    
    @NotNull(message = "Le cabinet est obligatoire")
    @Positive(message = "ID du cabinet invalide")
    private Long cabinetId;

}
