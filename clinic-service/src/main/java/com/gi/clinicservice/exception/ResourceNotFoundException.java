package com.gi.clinicservice.exception;

/**
 * Exception levée lorsqu'un cabinet est introuvable par ID.
 * 
 * <p><b>Cas d'usage :</b></p>
 * <ul>
 *   <li>GET /api/clinics/{id} avec ID inexistant</li>
 *   <li>PUT /api/clinics/{id} ou PATCH /api/clinics/{id}/activate avec ID invalide</li>
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