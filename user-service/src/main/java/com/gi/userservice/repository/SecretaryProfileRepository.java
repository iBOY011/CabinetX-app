package com.gi.userservice.repository;

import com.gi.userservice.model.entity.SecretaryProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité SecretaryProfile.
 * 
 * <p>Gère les associations secrétaire ↔ clinique. 
 * Permet de vérifier la contrainte métier : 1 clinique = 1 secrétaire maximum.</p>
 * 
 * <p><b>Méthodes personnalisées :</b></p>
 * <ul>
 *   <li><b>findByUserId :</b> Récupère profil secrétaire par User.id (enrichissement UserDTO.clinicId)</li>
 *   <li><b>findByClinicId :</b> Vérifie si clinique a déjà une secrétaire (contrainte unicité)</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Repository
public interface SecretaryProfileRepository extends JpaRepository<SecretaryProfile, Long> {

    /**
     * Trouve le profil secrétaire associé à un utilisateur.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Optional contenant le profil si existe
     * 
     * <p><b>Cas d'usage :</b> Après login secrétaire, charger clinicId pour redirection Dashboard</p>
     */
    Optional<SecretaryProfile> findByUserId(Long userId);

    /**
     * Trouve tous les profils secrétaires associés à une clinique.
     * 
     * @param clinicId L'identifiant de la clinique
     * @return Liste des profils (normalement 0 ou 1 élément max)
     * 
     * <p><b>Cas d'usage :</b> Avant création SECRETAIRE, vérifier findByClinicId(X).isEmpty() pour enforce contrainte</p>
     */
    List<SecretaryProfile> findByClinicId(Long clinicId);
}
