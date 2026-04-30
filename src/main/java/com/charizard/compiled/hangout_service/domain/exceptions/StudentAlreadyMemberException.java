package com.charizard.compiled.hangout_service.domain.exceptions;

public class StudentAlreadyMemberException extends RuntimeException {
    public StudentAlreadyMemberException(String message) {
        super(message);
    }
}
