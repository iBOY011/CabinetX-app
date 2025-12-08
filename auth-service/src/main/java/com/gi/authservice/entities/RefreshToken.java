package com.gi.authservice.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private Long utilisateurId;

    @Column(nullable = false)
    private LocalDateTime dateExpiration;

    private LocalDateTime dateRevocation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private boolean estRevoque;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(dateExpiration);
    }

    public boolean isRevoked() {
        return estRevoque || dateRevocation != null;
    }

    @PrePersist
    void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
