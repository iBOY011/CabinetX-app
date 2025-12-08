package com.gi.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token requis") String refreshToken
) {
}
