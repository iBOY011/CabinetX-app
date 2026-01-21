package com.gi.medicationservice.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de transfert pour les opérations CRUD sur les médicaments.
 * 
 * <p><b>Validations :</b></p>
 * <ul>
 *   <li><b>commercialName :</b> @NotBlank, @Size(2-100)</li>
 *   <li><b>dci :</b> @NotBlank, @Size(2-100) (Dénomination Commune Internationale obligatoire)</li>
 *   <li><b>dosage :</b> @NotBlank, @Pattern (format: nombre + unité ex: 500mg, 10ml, 2.5g)</li>
 *   <li><b>form :</b> @NotBlank, @Size(2-50) (Comprimé, Gélule, Sirop, etc.)</li>
 * </ul>
 * 
 * <p><b>Formats dosage acceptés :</b></p>
 * <ul>
 *   <li>mg, g (masse)</li>
 *   <li>ml, L (volume)</li>
 *   <li>mcg, µg (microgrammes)</li>
 *   <li>UI (Unités Internationales)</li>
 *   <li>% (pourcentage)</li>
 * </ul>
 * 
 * <p><b>Exemple JSON :</b></p>
 * <pre>
 * {
 *   "id": 1,
 *   "commercialName": "Amoxicilline Biogaran",
 *   "dci": "Amoxicilline",
 *   "dosage": "1g",
 *   "form": "Comprimé pelliculé"
 * }
 * </pre>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {

    private Long id;

    @NotBlank(message = "Le nom commercial est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom commercial doit contenir entre 2 et 100 caractères")
    private String commercialName;

    @NotBlank(message = "La DCI (Dénomination Commune Internationale) est obligatoire")
    @Size(min = 2, max = 100, message = "La DCI doit contenir entre 2 et 100 caractères")
    private String dci;

    @NotBlank(message = "Le dosage est obligatoire")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?\\s*(mg|g|ml|L|mcg|µg|UI|%)?$", 
             message = "Format de dosage invalide (ex: 500mg, 10ml, 2.5g)")
    private String dosage;

    @NotBlank(message = "La forme pharmaceutique est obligatoire")
    @Size(min = 2, max = 50, message = "La forme doit contenir entre 2 et 50 caractères")
    private String form;
}