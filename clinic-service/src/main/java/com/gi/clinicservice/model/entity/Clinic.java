package com.gi.clinicservice.model.entity;

import com.gi.clinicservice.model.enums.ClinicStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité JPA représentant un cabinet médical.
 * 
 * <p>Un cabinet est une structure médicale (cabinet dentaire, dermatologique, etc.) 
 * avec un abonnement limité dans le temps (serviceEndDate). Le statut (ACTIVE/INACTIVE) 
 * contrôle l'accès aux fonctionnalités de l'application.</p>
 * 
 * <p><b>Champs clés :</b></p>
 * <ul>
 *   <li><b>id :</b> Identifiant unique généré automatiquement</li>
 *   <li><b>name :</b> Nom du cabinet (2-100 caractères)</li>
 *   <li><b>specialty :</b> Spécialité médicale (Dentisterie, Dermatologie, etc.)</li>
 *   <li><b>phone :</b> Téléphone marocain (+212612345678 ou 0612345678)</li>
 *   <li><b>address :</b> Adresse postale complète</li>
 *   <li><b>logoUrl :</b> URL du logo (optionnel, pour personnalisation UI)</li>
 *   <li><b>status :</b> ACTIVE (abonnement valide) | INACTIVE (abonnement expiré)</li>
 *   <li><b>serviceEndDate :</b> Date fin abonnement (alertes à J-30)</li>
 * </ul>
 * 
 * <p><b>Relations :</b></p>
 * <ul>
 *   <li>1 Clinic ↔ N Users (médecins, secrétaires)</li>
 *   <li>1 Clinic ↔ N Patients</li>
 *   <li>1 Clinic ↔ N Appointments</li>
 * </ul>
 * 
 * <p><b>Table PostgreSQL :</b> clinic</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private String address;
    private String logoUrl;
    @Enumerated(EnumType.STRING)
    private ClinicStatus status;
    private LocalDate serviceEndDate;
}