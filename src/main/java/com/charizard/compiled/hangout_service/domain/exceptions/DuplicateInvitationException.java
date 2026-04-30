package com.charizard.compiled.hangout_service.domain.exceptions;

public class DuplicateInvitationException extends RuntimeException {
    public DuplicateInvitationException(String message) {
        super(message);
    }
}
