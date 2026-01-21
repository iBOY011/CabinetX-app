package com.gi.userservice.repository;

import com.gi.userservice.model.entity.User;
import com.gi.userservice.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité User.
 * 
 * <p>Fournit les méthodes de requête pour accéder aux utilisateurs dans PostgreSQL.
 * Inclut recherche par login (email) et par rôle.</p>
 * 
 * <p><b>Méthodes personnalisées :</b></p>
 * <ul>
 *   <li><b>findByLogin :</b> Recherche utilisateur par email (unique, utilisé pour authentification)</li>
 *   <li><b>findByRole :</b> Filtre utilisateurs par rôle (MEDCIN, SECRETAIRE, ADMIN)</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son login (email).
     * 
     * @param login L'email de l'utilisateur (champ unique)
     * @return Optional contenant l'utilisateur si trouvé
     * 
     * <p><b>Cas d'usage :</b> Vérification doublons lors création, login POST /auth/login</p>
     */
    Optional<User> findByLogin(String login);

    /**
     * Trouve tous les utilisateurs ayant un rôle spécifique.
     * 
     * @param role Le rôle recherché (MEDCIN, SECRETAIRE, ADMIN)
     * @return Liste des utilisateurs avec ce rôle
     * 
     * <p><b>Cas d'usage :</b> GET /api/users/role/MEDCIN (liste tous médecins pour admin)</p>
     */
    List<User> findByRole(UserRole role);
}