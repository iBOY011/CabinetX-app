package com.gi.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Identifiant requis") String identifiant,
        @NotBlank(message = "Mot de passe requis") String motDePasse
) {
}
