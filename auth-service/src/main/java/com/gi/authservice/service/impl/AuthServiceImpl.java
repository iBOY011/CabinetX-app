package com.gi.authservice.service.impl;

import com.gi.authservice.dto.ChangePasswordRequest;
import com.gi.authservice.dto.LoginRequest;
import com.gi.authservice.dto.LoginResponse;
import com.gi.authservice.dto.RefreshTokenRequest;
import com.gi.authservice.dto.RegisterRequest;
import com.gi.authservice.entities.Compte;
import com.gi.authservice.entities.RefreshToken;
import com.gi.authservice.enums.AccountStatus;
import com.gi.authservice.exception.AccountLockedException;
import com.gi.authservice.exception.InvalidCredentialsException;
import com.gi.authservice.exception.TokenExpiredException;
import com.gi.authservice.exception.UserAlreadyExistsException;
import com.gi.authservice.repository.CompteRepository;
import com.gi.authservice.service.AuditService;
import com.gi.authservice.service.AuthService;
import com.gi.authservice.service.JwtTokenService;
import com.gi.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final CompteRepository compteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

    @Override
    public LoginResponse login(LoginRequest request, String adresseIp) {
        Compte compte = compteRepository.findByLogin(request.identifiant())
                .or(() -> compteRepository.findByEmail(request.identifiant()))
                .orElseThrow(() -> new InvalidCredentialsException("Identifiants invalides"));

        validerLoginAttempt(compte);

        if (!passwordEncoder.matches(request.motDePasse(), compte.getMotDePasseHash())) {
            compte.incrementTentativesEchec();
            if (compte.getTentativesEchec() >= MAX_LOGIN_ATTEMPTS) {
                compte.verrouiller();
                compteRepository.save(compte);
                auditService.logLoginFailure(request.identifiant(), adresseIp, "Compte verrouillé");
                throw new AccountLockedException("Compte verrouillé après tentatives échouées");
            }
            compteRepository.save(compte);
            auditService.logLoginFailure(request.identifiant(), adresseIp, "Mot de passe incorrect");
            throw new InvalidCredentialsException("Identifiants invalides");
        }

        compte.reinitialiserTentativesEchec();
        compte.setDerniereConnexion(LocalDateTime.now());
        if (compte.getStatus() == AccountStatus.PENDING_VERIFICATION) {
            compte.setStatus(AccountStatus.ACTIVE);
        }
        compteRepository.save(compte);

        List<String> permissions = new ArrayList<>(compte.getPermissions());
        String accessToken = jwtTokenService.genererToken(compte, compte.getRole(), permissions);
        RefreshToken refreshToken = refreshTokenService.creerRefreshToken(compte.getUtilisateurId());
        auditService.logLoginSuccess(compte.getUtilisateurId(), adresseIp);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiration(Instant.now().plusMillis(jwtTokenService.getExpirationTime()))
                .utilisateurId(compte.getUtilisateurId())
                .role(compte.getRole())
                .permissions(permissions)
                .build();
    }

    @Override
    public Compte register(RegisterRequest request) {
        if (compteRepository.existsByLogin(request.login()) || compteRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Un utilisateur avec cet identifiant existe déjà");
        }
        Compte compte = Compte.builder()
                .utilisateurId(request.utilisateurId())
                .login(request.login())
                .email(request.email())
                .motDePasseHash(passwordEncoder.encode(request.motDePasse()))
                .role(request.role() == null ? "USER" : request.role())
                .status(AccountStatus.ACTIVE)
                .build();
        if (request.permissions() != null && !request.permissions().isEmpty()) {
            compte.getPermissions().addAll(request.permissions());
        }
        return compteRepository.save(compte);
    }

    @Override
    public void logout(Long utilisateurId) {
        refreshTokenService.revoquerTousLesTokens(utilisateurId);
        auditService.logLogout(utilisateurId);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.trouverParToken(request.refreshToken());
        if (token.isExpired() || token.isRevoked()) {
            refreshTokenService.revoquerToken(token.getToken());
            throw new TokenExpiredException("Refresh token expiré");
        }
        Compte compte = compteRepository.findByUtilisateurId(token.getUtilisateurId())
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur introuvable"));
        String accessToken = jwtTokenService.genererToken(compte, compte.getRole(), new ArrayList<>(compte.getPermissions()));
        RefreshToken nouveau = refreshTokenService.creerRefreshToken(token.getUtilisateurId());
        refreshTokenService.revoquerToken(token.getToken());
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(nouveau.getToken())
                .expiration(Instant.now().plusMillis(jwtTokenService.getExpirationTime()))
                .utilisateurId(compte.getUtilisateurId())
                .role(compte.getRole())
                .permissions(new ArrayList<>(compte.getPermissions()))
                .build();
    }

    @Override
    public void changePassword(Long utilisateurId, ChangePasswordRequest request) {
        Compte compte = compteRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur introuvable"));
        if (!passwordEncoder.matches(request.ancienMotDePasse(), compte.getMotDePasseHash())) {
            throw new InvalidCredentialsException("Ancien mot de passe incorrect");
        }
        compte.setMotDePasseHash(passwordEncoder.encode(request.nouveauMotDePasse()));
        compteRepository.save(compte);
        auditService.logPasswordChange(utilisateurId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierAcces(Long utilisateurId, String permission) {
        return compteRepository.findByUtilisateurId(utilisateurId)
                .filter(compte -> !compte.estVerrouille())
                .map(compte -> compte.getPermissions().contains(permission))
                .orElse(false);
    }

    @Override
    public void validerLoginAttempt(Compte compte) {
        if (compte.estVerrouille()) {
            throw new AccountLockedException("Compte verrouillé");
        }
    }
}
