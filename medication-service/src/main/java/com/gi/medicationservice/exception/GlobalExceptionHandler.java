package com.gi.medicationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour le microservice Medication.
 * 
 * <p>Intercepte les exceptions levées dans les contrôleurs et services, 
 * les transforme en réponses HTTP standardisées avec codes appropriés.</p>
 * 
 * <p><b>Exceptions traitées :</b></p>
 * <ul>
 *   <li><b>ResourceNotFoundException :</b> 404 Not Found (médicament inexistant)</li>
 *   <li><b>Exception :</b> 500 Internal Server Error (erreurs génériques)</li>
 * </ul>
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
     * @param ex L'exception levée
     * @return Réponse HTTP 404 avec message d'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}