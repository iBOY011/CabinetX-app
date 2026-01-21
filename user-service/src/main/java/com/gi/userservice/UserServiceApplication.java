package com.gi.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Point d'entrée du microservice User.
 * 
 * <p><b>Responsabilités :</b></p>
 * <ul>
 *   <li>Gestion des utilisateurs (médecins, secrétaires, admins) - CRUD</li>
 *   <li>Intégration Keycloak pour SSO (création comptes + synchronisation rôles)</li>
 *   <li>Gestion profils (DoctorProfile, SecretaryProfile)</li>
 *   <li>Enforcement contrainte métier : 1 clinique = 1 médecin + 1 secrétaire max</li>
 * </ul>
 * 
 * <p><b>Base de données :</b> PostgreSQL - user_db (port 5437)</p>
 * 
 * <p><b>Port serveur :</b> 8084</p>
 * 
 * <p><b>Eureka :</b> Enregistré comme USER-SERVICE</p>
 * 
 * <p><b>Intégration Keycloak :</b></p>
 * <ul>
 *   <li>OAuth2 Resource Server (validation JWT)</li>
 *   <li>Keycloak Admin Client (création utilisateurs + rôles)</li>
 *   <li>Realm: cabinetx-realm, Roles: MEDCIN, SECRETAIRE, ADMIN</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}