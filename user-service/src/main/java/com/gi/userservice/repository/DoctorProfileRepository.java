package com.gi.userservice.repository;

import com.gi.userservice.model.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité DoctorProfile.
 * 
 * <p>Gère les associations médecin ↔ clinique. 
 * Permet de vérifier la contrainte métier : 1 clinique = 1 médecin maximum.</p>
 * 
 * <p><b>Méthodes personnalisées :</b></p>
 * <ul>
 *   <li><b>findByUserId :</b> Récupère profil médecin par User.id (enrichissement UserDTO.clinicId)</li>
 *   <li><b>findByClinicId :</b> Vérifie si clinique a déjà un médecin (contrainte unicité)</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    /**
     * Trouve le profil médecin associé à un utilisateur.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Optional contenant le profil si existe
     * 
     * <p><b>Cas d'usage :</b> Après login médecin, charger clinicId pour redirection Dashboard</p>
     */
    Optional<DoctorProfile> findByUserId(Long userId);

    /**
     * Trouve tous les profils médecins associés à une clinique.
     * 
     * @param clinicId L'identifiant de la clinique
     * @return Liste des profils (normalement 0 ou 1 élément max)
     * 
     * <p><b>Cas d'usage :</b> Avant création MEDECIN, vérifier findByClinicId(X).isEmpty() pour enforce contrainte</p>
     */
    List<DoctorProfile> findByClinicId(Long clinicId);
}