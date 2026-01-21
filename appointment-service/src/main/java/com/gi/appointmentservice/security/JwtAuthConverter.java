package com.gi.appointmentservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convertisseur personnalisé pour extraire les rôles Keycloak depuis les tokens JWT.
 * 
 * <p>Spring Security par défaut n'extrait que les scopes OAuth2. Ce converter extrait
 * également les rôles depuis le claim "realm_access.roles" de Keycloak.
 * 
 * <p>Structure du JWT Keycloak :
 * <pre>
 * {
 *   "preferred_username": "medecin1",
 *   "realm_access": {
 *     "roles": ["MEDECIN", "USER"]
 *   },
 *   "scope": "openid profile email"
 * }
 * </pre>
 * 
 * <p>Transformation appliquée :
 * <ul>
 *   <li>Rôles Keycloak (realm_access.roles) → Spring Security GrantedAuthority</li>
 *   <li>Username : Utilise "preferred_username" du JWT</li>
 *   <li>Fusion : Scopes OAuth2 + Rôles Keycloak</li>
 * </ul>
 * 
 * <p>Utilisation avec @PreAuthorize :
 * <pre>
 * @PreAuthorize("hasRole('MEDECIN')")
 * public RDVResponse updateStatus(...) { ... }
 * </pre>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();
    
    /**
     * Convertit un JWT en AbstractAuthenticationToken avec rôles Keycloak.
     * 
     * <p>Fusionne les authorities par défaut (scopes OAuth2) avec les rôles
     * extraits du claim realm_access.roles de Keycloak.
     * 
     * @param jwt le token JWT à convertir
     * @return JwtAuthenticationToken avec authorities complètes et username
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities,jwt.getClaim("preferred_username"));
    }
    /**
     * Extrait les rôles Keycloak depuis le claim realm_access.roles.
     * 
     * <p>Parcourt le claim "realm_access" pour récupérer la liste des rôles
     * et les convertir en GrantedAuthority Spring Security.
     * 
     * @param jwt le token JWT contenant les claims
     * @return collection de GrantedAuthority (rôles Keycloak), vide si aucun rôle
     */
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String , Object> realmAccess;
        Collection<String> roles;
        if(jwt.getClaim("realm_access")==null){
            return Set.of();
        }
        realmAccess = jwt.getClaim("realm_access");
        roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream().map(role->new SimpleGrantedAuthority(role)).collect(Collectors.toSet());
    }

}