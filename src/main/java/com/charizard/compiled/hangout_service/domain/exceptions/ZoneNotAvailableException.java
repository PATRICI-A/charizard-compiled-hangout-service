package com.charizard.compiled.hangout_service.domain.exceptions;

public class ZoneNotAvailableException extends RuntimeException {
    public ZoneNotAvailableException(String message) {
        super(message);
    }
}
