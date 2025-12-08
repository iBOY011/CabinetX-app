package com.gi.authservice.service;

import com.gi.authservice.entities.Compte;

import java.util.List;

public interface JwtTokenService {
    String genererToken(Compte compte, String role, List<String> permissions);

    boolean validerToken(String token);

    Long extraireUtilisateurId(String token);

    String extraireRole(String token);

    long getExpirationTime();
}
