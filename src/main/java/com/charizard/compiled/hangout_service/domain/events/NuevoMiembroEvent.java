package com.charizard.compiled.hangout_service.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio (record) publicado cuando un nuevo estudiante se une a un parche,
 * ya sea directamente (parche público) o mediante aceptación de invitación (parche privado).
 *
 * @param capitanId    ID del capitán del parche
 * @param estudianteId ID del estudiante que se unió
 * @param nombreParche Nombre del parche al que se unió
 * @param timestamp    Fecha y hora del evento
 */
public record NuevoMiembroEvent(
        UUID capitanId,
        UUID estudianteId,
        String nombreParche,
        LocalDateTime timestamp
) {}
