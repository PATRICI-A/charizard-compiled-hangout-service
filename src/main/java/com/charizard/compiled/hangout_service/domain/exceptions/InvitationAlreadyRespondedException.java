package com.charizard.compiled.hangout_service.domain.exceptions;

public class InvitationAlreadyRespondedException extends RuntimeException {

    public InvitationAlreadyRespondedException() {
        super("La invitación ya fue respondida");
    }
}
