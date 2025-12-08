package com.gi.authservice.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken,
        Instant expiration,
        Long utilisateurId,
        String role,
        List<String> permissions
) {
}
