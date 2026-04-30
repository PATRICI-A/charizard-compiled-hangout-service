package com.charizard.compiled.hangout_service.domain.exceptions;

public class LimiteParchesAlcanzadoException extends RuntimeException {

    public LimiteParchesAlcanzadoException() {
        super("Student has reached maximum active patches limit (5)");
    }
}
