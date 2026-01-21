package com.gi.clinicservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour le microservice Clinic.
 * 
 * <p>Intercepte les exceptions levées dans les contrôleurs et services, 
 * les transforme en réponses HTTP standardisées avec codes appropriés.</p>
 * 
 * <p><b>Exceptions traitées :</b></p>
 * <ul>
 *   <li><b>ResourceNotFoundException :</b> 404 Not Found (cabinet inexistant)</li>
 *   <li><b>BusinessException :</b> 400 Bad Request (règle métier violée)</li>
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
     * @param e L'exception levée
     * @return Réponse HTTP 404 avec message d'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}