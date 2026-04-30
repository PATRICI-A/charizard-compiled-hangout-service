package com.charizard.compiled.hangout_service.entrypoints.advice;

import com.charizard.compiled.hangout_service.domain.exceptions.ConstraintViolationException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException;
import com.charizard.compiled.hangout_service.domain.exceptions.InvitationAlreadyRespondedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Global exception handler for the invitations module.
 * Converts domain exceptions into appropriate HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvitationAlreadyRespondedException.class)
    public ResponseEntity<Map<String, String>> handleInvitationAlreadyResponded(InvitationAlreadyRespondedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MaxHangoutsReachedException.class)
    public ResponseEntity<Map<String, String>> handleMaxHangoutsReached(MaxHangoutsReachedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(StudentAlreadyMemberException.class)
    public ResponseEntity<Map<String, String>> handleStudentAlreadyMember(StudentAlreadyMemberException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateInvitationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateInvitation(DuplicateInvitationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason()));
    }
}
