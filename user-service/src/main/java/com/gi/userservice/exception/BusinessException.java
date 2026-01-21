package com.gi.userservice.exception;

/**
 * Exception levée lors de la violation d'une règle métier.
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>Tentative d'assigner 2e médecin à une clinique (contrainte 1 clinique = 1 médecin)</li>
 *   <li>Tentative d'assigner 2e secrétaire à une clinique (contrainte 1 clinique = 1 secrétaire)</li>
 *   <li>Création utilisateur avec email dupliqué</li>
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