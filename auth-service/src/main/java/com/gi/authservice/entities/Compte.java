package com.gi.authservice.entities;

import com.gi.authservice.enums.AccountStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comptes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long utilisateurId;

    @Column(nullable = false, unique = true, length = 120)
    private String login;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String motDePasseHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Builder.Default
    @Column(nullable = false)
    private int tentativesEchec = 0;

    @Builder.Default
    @Column(nullable = false, length = 80)
    private String role = "USER";

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "compte_permissions", joinColumns = @JoinColumn(name = "compte_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    private LocalDateTime derniereConnexion;
    private LocalDateTime derniereVerification;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private LocalDateTime dateModification;

    public void incrementTentativesEchec() {
        tentativesEchec++;
    }

    public void reinitialiserTentativesEchec() {
        tentativesEchec = 0;
    }

    public void verrouiller() {
        status = AccountStatus.LOCKED;
    }

    public void deverrouiller() {
        status = AccountStatus.ACTIVE;
        tentativesEchec = 0;
    }

    public boolean estVerrouille() {
        return status == AccountStatus.LOCKED;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        dateCreation = now;
        dateModification = now;
        if (status == null) {
            status = AccountStatus.PENDING_VERIFICATION;
        }
    }

    @PreUpdate
    void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
