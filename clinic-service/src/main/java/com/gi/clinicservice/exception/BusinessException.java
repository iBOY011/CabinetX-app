package com.gi.clinicservice.exception;

/**
 * Exception levée lors de la violation d'une règle métier.
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>Tentative de créer un cabinet avec serviceEndDate dans le passé</li>
 *   <li>Tentative d'activer un cabinet sans renouveler l'abonnement</li>
 * </ul>
 * 
 * <p><b>Gestion :</b> Interceptée par GlobalExceptionHandler → HTTP 400 Bad Request</p>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}