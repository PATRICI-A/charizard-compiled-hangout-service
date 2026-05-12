package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta agregar un miembro a un parche
 * que ya alcanzó su cupo máximo de participantes.
 * Retorna HTTP 409 Conflict.
 */
public class MaximumCapacityReachedException extends RuntimeException {
    public MaximumCapacityReachedException(String message) {
        super(message);
    }
}
