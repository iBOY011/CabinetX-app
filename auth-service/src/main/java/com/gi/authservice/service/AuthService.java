package com.gi.authservice.service;

import com.gi.authservice.dto.ChangePasswordRequest;
import com.gi.authservice.dto.LoginRequest;
import com.gi.authservice.dto.LoginResponse;
import com.gi.authservice.dto.RefreshTokenRequest;
import com.gi.authservice.dto.RegisterRequest;
import com.gi.authservice.entities.Compte;

public interface AuthService {
    LoginResponse login(LoginRequest request, String adresseIp);

    Compte register(RegisterRequest request);

    void logout(Long utilisateurId);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void changePassword(Long utilisateurId, ChangePasswordRequest request);

    boolean verifierAcces(Long utilisateurId, String permission);

    void validerLoginAttempt(Compte compte);
}
