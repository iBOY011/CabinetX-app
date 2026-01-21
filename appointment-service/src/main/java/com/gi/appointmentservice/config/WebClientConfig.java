package com.gi.appointmentservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration pour les clients HTTP WebClient avec load balancing.
 * 
 * <p>Configure un WebClient.Builder avec les capacités suivantes :
 * <ul>
 *   <li>@LoadBalanced : Active le load balancing client-side via Spring Cloud LoadBalancer</li>
 *   <li>Service Discovery : Résolution des noms de services via Eureka</li>
 *   <li>Retry et Circuit Breaker : (TODO) Intégrer Resilience4j</li>
 * </ul>
 * 
 * <p>Utilisation dans les clients :
 * <pre>
 * public PatientClient(WebClient.Builder builder) {
 *     this.webClient = builder.build();
 * }
 * 
 * // Les URLs utilisent le nom du service au lieu de host:port
 * webClient.get().uri("http://PATIENT-SERVICE/api/patients/{id}", id)
 * </pre>
 * 
 * <p>Avantages :
 * <ul>
 *   <li>Découplage : Pas besoin de connaître les IPs des services</li>
 *   <li>Scalabilité : Load balancing automatique entre instances</li>
 *   <li>Résilience : Retry automatique sur les instances disponibles</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Configuration
public class WebClientConfig {

    /**
     * Crée un WebClient.Builder avec load balancing activé.
     * 
     * <p>@LoadBalanced permet de résoudre les noms de services (ex: PATIENT-SERVICE)
     * en adresses réelles via Eureka et de répartir les requêtes entre instances.
     * 
     * @return WebClient.Builder configuré pour service discovery
     */
    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
