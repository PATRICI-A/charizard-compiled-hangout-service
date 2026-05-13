package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando un estudiante intenta crear o unirse a un parche
 * pero ya alcanzó el límite máximo de 5 parches activos simultáneos.
 * Retorna HTTP 409 Conflict.
 */
public class MaxHangoutsReachedException extends RuntimeException {

    public MaxHangoutsReachedException() {
        super("Student has reached maximum active patches limit (5)");
    }
}
