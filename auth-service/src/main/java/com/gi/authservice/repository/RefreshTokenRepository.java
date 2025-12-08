package com.gi.authservice.repository;

import com.gi.authservice.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUtilisateurId(Long utilisateurId);

    void deleteByUtilisateurId(Long utilisateurId);
}
