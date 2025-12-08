package com.gi.authservice.repository;

import com.gi.authservice.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte, Long> {

    Optional<Compte> findByLogin(String login);

    Optional<Compte> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    Optional<Compte> findByUtilisateurId(Long utilisateurId);
}
