package com.gi.userservice.exception;

/**
 * Exception levée lorsqu'un utilisateur est introuvable.
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>GET /api/users/{id} avec ID inexistant</li>
 *   <li>GET /api/users/login/{email} avec email inexistant</li>
 *   <li>PUT /api/users/{id} sur utilisateur supprimé</li>
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