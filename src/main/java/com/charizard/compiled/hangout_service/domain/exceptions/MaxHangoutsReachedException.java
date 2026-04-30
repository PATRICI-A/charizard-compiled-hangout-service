package com.charizard.compiled.hangout_service.domain.exceptions;

public class MaxHangoutsReachedException extends RuntimeException {

    public MaxHangoutsReachedException() {
        super("Student has reached maximum active patches limit (5)");
    }
}
