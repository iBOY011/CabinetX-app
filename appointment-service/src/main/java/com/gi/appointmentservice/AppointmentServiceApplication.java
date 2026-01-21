package com.gi.appointmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale du microservice Appointment.
 * 
 * <p>Ce microservice gère l'ensemble du cycle de vie des rendez-vous médicaux :
 * <ul>
 *   <li>CRUD complet des rendez-vous</li>
 *   <li>Validation des règles métier (unicité, horaires, statuts)</li>
 *   <li>Gestion de la file d'attente virtuelle</li>
 *   <li>Intégration avec Patient Service pour enrichissement des données</li>
 *   <li>Notifications via Kafka (changements de statut, rappels)</li>
 * </ul>
 * 
 * <p>Architecture technique :
 * <ul>
 *   <li>Port : 8082</li>
 *   <li>Base de données : PostgreSQL (appointment_db)</li>
 *   <li>Sécurité : OAuth2/JWT via Keycloak</li>
 *   <li>Découverte : Eureka Service Discovery</li>
 *   <li>Messaging : Apache Kafka (topic : appointment-events)</li>
 * </ul>
 * 
 * <p>Fonctionnalités principales :
 * <ul>
 *   <li>API REST : /api/appointments (gestion RDV)</li>
 *   <li>API REST : /api/queue (gestion file d'attente)</li>
 *   <li>Consumers Kafka : welcomeConsumer (messages entrants)</li>
 *   <li>Producers Kafka : événements rendez-vous</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@SpringBootApplication
public class AppointmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentServiceApplication.class, args);
    }

}
