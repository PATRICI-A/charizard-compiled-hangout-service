package com.charizard.compiled.hangout_service.domain.exceptions;

public class MaximumCapacityReachedException extends RuntimeException {
    public MaximumCapacityReachedException(String message) {
        super(message);
    }
}
