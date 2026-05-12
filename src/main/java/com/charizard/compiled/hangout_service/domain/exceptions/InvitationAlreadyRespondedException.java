package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta responder una invitación que ya fue
 * aceptada o rechazada previamente.
 * Retorna HTTP 409 Conflict.
 */
public class InvitationAlreadyRespondedException extends RuntimeException {

    public InvitationAlreadyRespondedException() {
        super("La invitación ya fue respondida");
    }
}
