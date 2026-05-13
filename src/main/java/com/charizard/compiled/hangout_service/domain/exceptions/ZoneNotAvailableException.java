package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando un recurso de zona no está disponible
 * (reservado para futuras funcionalidades de ubicación).
 * Retorna HTTP 409 Conflict.
 */
public class ZoneNotAvailableException extends RuntimeException {
    public ZoneNotAvailableException(String message) {
        super(message);
    }
}
