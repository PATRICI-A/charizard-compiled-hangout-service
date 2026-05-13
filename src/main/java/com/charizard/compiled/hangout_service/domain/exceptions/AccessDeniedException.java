package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando un estudiante intenta realizar una operación
 * para la cual no tiene permisos (ej. modificar un parche del que no es capitán).
 * Retorna HTTP 403 Forbidden.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
