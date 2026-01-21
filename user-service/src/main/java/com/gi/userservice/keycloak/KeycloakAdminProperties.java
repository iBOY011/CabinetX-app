package com.gi.userservice.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Propriétés de configuration pour Keycloak Admin API.
 * 
 * <p>Charge les paramètres depuis application.yml avec préfixe <code>keycloak.admin</code> :</p>
 * <ul>
 *   <li><b>server-url :</b> URL base Keycloak (ex: https://keycloak.example.com)</li>
 *   <li><b>realm :</b> Nom du realm (ex: cabinetx-realm)</li>
 *   <li><b>client-id :</b> Client ID service account (ex: cabinetx-admin-client)</li>
 *   <li><b>client-secret :</b> Secret du client (ex: xxxx-yyyy-zzzz)</li>
 * </ul>
 * 
 * <p><b>Méthode isConfigured() :</b></p>
 * <ul>
 *   <li>Retourne true si TOUS les champs sont renseignés</li>
 *   <li>Retourne false si au moins un champ vide → Mode dev sans Keycloak</li>
 * </ul>
 * 
 * <p><b>Exemple application.yml :</b></p>
 * <pre>
 * keycloak:
 *   admin:
 *     server-url: http://localhost:8080
 *     realm: cabinetx
 *     client-id: admin-cli
 *     client-secret: secret123
 * </pre>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@ConfigurationProperties(prefix = "keycloak.admin")
public class KeycloakAdminProperties {

    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isConfigured() {
        return StringUtils.hasText(serverUrl)
                && StringUtils.hasText(realm)
                && StringUtils.hasText(clientId)
                && StringUtils.hasText(clientSecret);
    }
}
