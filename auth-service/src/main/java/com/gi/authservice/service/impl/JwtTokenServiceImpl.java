package com.gi.authservice.service.impl;

import com.gi.authservice.entities.Compte;
import com.gi.authservice.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final String secret;
    private final long expirationMs;
    private SecretKey secretKey;

    public JwtTokenServiceImpl(@Value("${security.jwt.secret:change-me-secret}") String secret,
                               @Value("${security.jwt.expiration:900000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String genererToken(Compte compte, String role, List<String> permissions) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(compte.getUtilisateurId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .claim("role", role)
                .claim("permissions", permissions)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validerToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Long extraireUtilisateurId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    @Override
    public String extraireRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    @Override
    public long getExpirationTime() {
        return expirationMs;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
