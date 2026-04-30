package com.charizard.compiled.hangout_service.domain.exceptions;

public class InvitacionYaRespondidaException extends RuntimeException {

    public InvitacionYaRespondidaException() {
        super("La invitación ya fue respondida");
    }
}
