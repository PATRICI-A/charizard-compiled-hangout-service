package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando se viola una restricción de integridad de datos
 * (ej. datos inconsistentes en la base de datos).
 * Retorna HTTP 400 Bad Request.
 */
public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String message) {
        super(message);
    }
}
