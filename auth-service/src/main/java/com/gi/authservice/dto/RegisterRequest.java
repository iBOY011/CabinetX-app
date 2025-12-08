package com.gi.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RegisterRequest(
        @NotNull(message = "Identifiant utilisateur requis") Long utilisateurId,
        @NotBlank(message = "Login requis") String login,
        @Email(message = "Email invalide") @NotBlank(message = "Email requis") String email,
        @NotBlank(message = "Mot de passe requis") String motDePasse,
        String role,
        List<String> permissions
) {
}
