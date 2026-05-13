package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando un estudiante intenta unirse a un parche
 * del cual ya es miembro.
 * Retorna HTTP 409 Conflict.
 */
public class StudentAlreadyMemberException extends RuntimeException {
    public StudentAlreadyMemberException(String message) {
        super(message);
    }
}
