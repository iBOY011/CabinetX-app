package com.gi.authservice.service;

public interface AuditService {
    void logLoginSuccess(Long utilisateurId, String adresseIp);

    void logLoginFailure(String identifiant, String adresseIp, String raison);

    void logLogout(Long utilisateurId);

    void logPasswordChange(Long utilisateurId);
}
