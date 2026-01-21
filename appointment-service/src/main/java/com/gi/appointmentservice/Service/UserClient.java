package com.gi.appointmentservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.appointmentservice.Model.DTO.UserInfoDTO;

import java.util.List;

/**
 * Client HTTP pour la communication avec le microservice User.
 * 
 * <p>Permet de récupérer les informations des utilisateurs (médecins, secrétaires)
 * pour enrichir les notifications et affichages de la file d'attente.
 * 
 * <p>Méthodes disponibles :
 * <ul>
 *   <li>getUsersByClinic : Récupère tous les utilisateurs d'un cabinet</li>
 *   <li>getUserById : Récupère un utilisateur spécifique par son ID</li>
 * </ul>
 * 
 * <p>Pattern architectural :
 * <ul>
 *   <li>Service Discovery : Résolution "USER-SERVICE" via Eureka</li>
 *   <li>Load Balancing : Répartition automatique entre instances</li>
 *   <li>Appels synchrones : Utilise .block() pour simplicité</li>
 * </ul>
 * 
 * <p>Cas d'usage :
 * <ul>
 *   <li>Envoi de notifications au médecin lors d'un appel de patient</li>
 *   <li>Affichage du nom du médecin dans l'agenda</li>
 *   <li>Liste des médecins d'un cabinet pour assignation de RDV</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Récupère tous les utilisateurs (médecins, secrétaires) d'un cabinet.
     * 
     * <p>Utile pour :
     * <ul>
     *   <li>Afficher la liste des médecins disponibles</li>
     *   <li>Assigner un rendez-vous à un médecin spécifique</li>
     *   <li>Générer des statistiques par médecin</li>
     * </ul>
     * 
     * @param clinicId identifiant du cabinet médical
     * @return liste des utilisateurs du cabinet
     * @throws WebClientResponseException si le cabinet n'existe pas
     */
    public List<UserInfoDTO> getUsersByClinic(Long clinicId) {
        System.out.println("[UserClient] Calling user service for clinic ID: " + clinicId);

        List<UserInfoDTO> users = webClient.get()
                .uri("http://USER-SERVICE/api/users/clinic/{clinicId}", clinicId)
                .retrieve()
                .bodyToFlux(UserInfoDTO.class)
                .collectList()
                .block();

        System.out.println("[UserClient] Received " + (users != null ? users.size() : 0) + " users for clinic " + clinicId);
        return users;
    }

    /**
     * Récupère les informations d'un utilisateur par son identifiant.
     * 
     * <p>Utilisé pour obtenir le nom du médecin lors de l'envoi de notifications
     * ou l'affichage de l'agenda.
     * 
     * @param userId identifiant de l'utilisateur
     * @return UserInfoDTO contenant login, nom, rôle
     * @throws WebClientResponseException si l'utilisateur n'existe pas (404)
     */
    public UserInfoDTO getUserById(Long userId) {
        System.out.println("[UserClient] Calling user service for user ID: " + userId);

        UserInfoDTO user = webClient.get()
                .uri("http://USER-SERVICE/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserInfoDTO.class)
                .block();

        System.out.println("[UserClient] Received user: " + (user != null ? user.getLogin() : "null"));
        return user;
    }
}
