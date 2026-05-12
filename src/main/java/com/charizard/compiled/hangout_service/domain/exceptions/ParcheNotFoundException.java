package com.charizard.compiled.hangout_service.domain.exceptions;

/**
 * Excepción lanzada cuando se busca un parche por ID y no existe en la base de datos.
 * Retorna HTTP 404 Not Found.
 */
public class ParcheNotFoundException extends RuntimeException {
    public ParcheNotFoundException(String message) {
        super(message);
    }
}
