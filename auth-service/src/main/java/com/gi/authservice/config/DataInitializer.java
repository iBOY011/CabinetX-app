package com.gi.authservice.config;

import com.gi.authservice.entities.Compte;
import com.gi.authservice.enums.AccountStatus;
import com.gi.authservice.repository.CompteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CompteRepository compteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (compteRepository.count() > 0) {
            return;
        }
        Compte compte = Compte.builder()
                .utilisateurId(1L)
                .login("admin")
                .email("admin@cabinetx.dev")
                .motDePasseHash(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .status(AccountStatus.ACTIVE)
                .permissions(Set.of("CONSULTATION_READ", "CONSULTATION_WRITE", "USERS_ADMIN"))
                .build();
        compteRepository.save(compte);
    }
}
