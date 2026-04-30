package com.charizard.compiled.hangout_service.entrypoints.advice;

import com.charizard.compiled.hangout_service.domain.exceptions.ConstraintViolationException;
import com.charizard.compiled.hangout_service.domain.exceptions.EstudianteYaEsMiembroException;
import com.charizard.compiled.hangout_service.domain.exceptions.InvitacionDuplicadaException;
import com.charizard.compiled.hangout_service.domain.exceptions.InvitacionYaRespondidaException;
import com.charizard.compiled.hangout_service.domain.exceptions.LimiteParchesAlcanzadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Manejador global de excepciones para el módulo de invitaciones.
 * Convierte excepciones de dominio en códigos HTTP apropiados.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvitacionYaRespondidaException.class)
    public ResponseEntity<Map<String, String>> handleInvitacionYaRespondida(InvitacionYaRespondidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(LimiteParchesAlcanzadoException.class)
    public ResponseEntity<Map<String, String>> handleLimiteParchesAlcanzado(LimiteParchesAlcanzadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EstudianteYaEsMiembroException.class)
    public ResponseEntity<Map<String, String>> handleEstudianteYaEsMiembro(EstudianteYaEsMiembroException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvitacionDuplicadaException.class)
    public ResponseEntity<Map<String, String>> handleInvitacionDuplicada(InvitacionDuplicadaException ex) {
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
