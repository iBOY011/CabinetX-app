package com.gi.clinicservice.security;

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
 * Convertisseur JWT pour extraire les rôles Keycloak depuis les tokens JWT.
 * 
 * <p><b>Fonctionnement :</b></p>
 * <ol>
 *   <li>Récupère le JWT du header Authorization: Bearer {token}</li>
 *   <li>Extrait les rôles depuis le claim "realm_access.roles" (Keycloak)</li>
 *   <li>Convertit en GrantedAuthority Spring Security (ex: "MEDECIN", "ADMIN")</li>
 * </ol>
 * 
 * <p><b>Exemple JWT Keycloak :</b></p>
 * <pre>
 * {
 *   "realm_access": {
 *     "roles": ["ADMIN", "MEDECIN"]
 *   },
 *   "preferred_username": "dr.majidi"
 * }
 * </pre>
 * 
 * <p><b>Sortie :</b> JwtAuthenticationToken avec authorities [ADMIN, MEDECIN]</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());
        System.out.println("[JwtAuthConverter] Extracted authorities: " + authorities);
        return new JwtAuthenticationToken(jwt, authorities,jwt.getClaim("preferred_username"));
    }
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