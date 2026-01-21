package com.gi.medicationservice.exception;

/**
 * Exception levée lorsqu'un médicament est introuvable.
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>GET /api/medications/{id} avec ID inexistant</li>
 *   <li>PUT /api/medications/{id} sur médicament supprimé</li>
 *   <li>DELETE /api/medications/{id} sur médicament déjà supprimé</li>
 * </ul>
 * 
 * <p><b>Gestion :</b> Interceptée par GlobalExceptionHandler → HTTP 404 Not Found</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}