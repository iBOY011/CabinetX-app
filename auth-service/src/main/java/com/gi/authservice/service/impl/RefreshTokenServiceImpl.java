package com.gi.authservice.service.impl;

import com.gi.authservice.entities.RefreshToken;
import com.gi.authservice.exception.TokenExpiredException;
import com.gi.authservice.repository.RefreshTokenRepository;
import com.gi.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    @Override
    public RefreshToken creerRefreshToken(Long utilisateurId) {
        Duration ttl = Duration.ofMillis(refreshExpirationMs);
        LocalDateTime expiration = LocalDateTime.now().plusSeconds(ttl.getSeconds()).plusNanos(ttl.getNano());
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .utilisateurId(utilisateurId)
                .dateExpiration(expiration)
                .estRevoque(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validerRefreshToken(String token) {
        RefreshToken refreshToken = trouverParToken(token);
        return !refreshToken.isExpired() && !refreshToken.isRevoked();
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken trouverParToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenExpiredException("Refresh token invalide"));
    }

    @Override
    public void revoquerToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setEstRevoque(true);
            rt.setDateRevocation(LocalDateTime.now());
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    public void revoquerTousLesTokens(Long utilisateurId) {
        var tokens = refreshTokenRepository.findByUtilisateurId(utilisateurId);
        tokens.forEach(rt -> {
            rt.setEstRevoque(true);
            rt.setDateRevocation(LocalDateTime.now());
        });
        refreshTokenRepository.saveAll(tokens);
    }
}
