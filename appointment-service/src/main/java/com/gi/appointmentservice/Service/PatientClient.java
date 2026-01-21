package com.gi.appointmentservice.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;

/**
 * Client HTTP pour la communication avec le microservice Patient.
 * 
 * <p>Utilise Spring WebClient pour effectuer des appels REST synchrones vers
 * le service Patient via le service discovery (Eureka). Récupère les informations
 * complètes des patients pour enrichir les RDVResponse.
 * 
 * <p>Pattern architectural :
 * <ul>
 *   <li>Service Discovery : Résolution du nom "PATIENT-SERVICE" via Eureka</li>
 *   <li>Load Balancing : Répartition automatique entre instances Patient</li>
 *   <li>Circuit Breaker : (TODO) Ajouter Resilience4j pour tolérance aux pannes</li>
 * </ul>
 * 
 * <p>Note d'implémentation : Utilise actuellement .block() pour appels synchrones.
 * Pour améliorer les performances, envisager une approche réactive complète
 * avec Mono/Flux dans toute la chaîne de traitement.
 * 
 * <p>Gestion des erreurs :
 * <ul>
 *   <li>Patient non trouvé (404) : WebClientResponseException</li>
 *   <li>Service indisponible : WebClientRequestException</li>
 *   <li>Timeout : ReadTimeoutException</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Service
public class PatientClient {

    private final WebClient webClient;

    public PatientClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }


    /**
     * Récupère les informations d'un patient par son identifiant.
     * 
     * <p>Effectue un appel GET synchrone vers le microservice Patient.
     * L'URL est résolue dynamiquement via Eureka Service Discovery.
     * 
     * @param id identifiant unique du patient
     * @return PatientInfoDTO contenant nom, prénom, CIN, date de naissance
     * @throws WebClientResponseException si le patient n'existe pas (404)
     * @throws WebClientRequestException si le service Patient est indisponible
     */
    public PatientInfoDTO getPatientById(Long id) {
    System.out.println("Calling patient service for ID: " + id);

    PatientInfoDTO patient = webClient.get()
            .uri("http://PATIENT-SERVICE/api/patients/{id}", id)
            .retrieve()
            .bodyToMono(PatientInfoDTO.class)
            .block(); // blocking only for testing

    System.out.println("Received patient: " + patient);
    return patient;
}
}