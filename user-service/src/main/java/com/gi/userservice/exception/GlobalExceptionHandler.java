package com.gi.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour le microservice User.
 * 
 * <p>Intercepte les exceptions levées dans les contrôleurs et services, 
 * les transforme en réponses HTTP standardisées avec codes appropriés.</p>
 * 
 * <p><b>Exceptions traitées :</b></p>
 * <ul>
 *   <li><b>ResourceNotFoundException :</b> 404 Not Found (utilisateur inexistant)</li>
 *   <li><b>RuntimeException :</b> 400 Bad Request (erreurs génériques, contraintes métier)</li>
 * </ul>
 * 
 * <p><b>Exemple erreur :</b></p>
 * <pre>
 * HTTP 400 Bad Request
 * "Cette clinique a déjà un médecin assigné. Une clinique ne peut avoir qu'un seul médecin."
 * </pre>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestion des exceptions ResourceNotFoundException.
     * 
     * @param e L'exception levée
     * @return Réponse HTTP 404 avec message d'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}