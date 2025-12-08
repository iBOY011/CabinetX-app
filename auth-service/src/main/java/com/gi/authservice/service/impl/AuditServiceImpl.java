package com.gi.authservice.service.impl;

import com.gi.authservice.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditServiceImpl implements AuditService {
    @Override
    public void logLoginSuccess(Long utilisateurId, String adresseIp) {
        log.info("Connexion réussie pour utilisateur {} depuis {}", utilisateurId, adresseIp);
    }

    @Override
    public void logLoginFailure(String identifiant, String adresseIp, String raison) {
        log.warn("Échec de connexion pour {} depuis {}: {}", identifiant, adresseIp, raison);
    }

    @Override
    public void logLogout(Long utilisateurId) {
        log.info("Déconnexion utilisateur {}", utilisateurId);
    }

    @Override
    public void logPasswordChange(Long utilisateurId) {
        log.info("Changement de mot de passe pour utilisateur {}", utilisateurId);
    }
}
