package com.gi.authservice.service;

import com.gi.authservice.entities.RefreshToken;

public interface RefreshTokenService {
    RefreshToken creerRefreshToken(Long utilisateurId);

    boolean validerRefreshToken(String token);

    RefreshToken trouverParToken(String token);

    void revoquerToken(String token);

    void revoquerTousLesTokens(Long utilisateurId);
}
