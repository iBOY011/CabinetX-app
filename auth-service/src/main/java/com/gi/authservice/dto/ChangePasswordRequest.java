package com.gi.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Ancien mot de passe requis") String ancienMotDePasse,
        @NotBlank(message = "Nouveau mot de passe requis") String nouveauMotDePasse
) {
}
