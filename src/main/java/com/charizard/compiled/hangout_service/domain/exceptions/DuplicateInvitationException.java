package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta enviar una invitación a un estudiante
 * que ya tiene una invitación pendiente para el mismo parche.
 * Retorna HTTP 409 Conflict.
 */
public class DuplicateInvitationException extends RuntimeException {
    public DuplicateInvitationException(String message) {
        super(message);
    }
}
