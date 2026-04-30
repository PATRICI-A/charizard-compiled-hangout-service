package com.charizard.compiled.hangout_service.domain.model;

import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio que representa una invitación a un parche.
 * Esta clase es independiente de la infraestructura (sin anotaciones JPA).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitacion {
    /**
     * Identificador único de la invitación.
     */
    private UUID id;

    /**
     * ID del parche al que se invita.
     */
    private UUID parcheId;

    /**
     * ID del capitán que envía la invitación.
     */
    private UUID capitanId;

    /**
     * ID del estudiante invitado.
     */
    private UUID estudianteInvitadoId;

    /**
     * Estado actual de la invitación (PENDIENTE, ACEPTADA, RECHAZADA).
     */
    private EstadoInvitacion estado;

    /**
     * Fecha y hora en que se envió la invitación.
     */
    private LocalDateTime fechaEnvio;

    /**
     * Fecha y hora en que se respondió la invitación.
     */
    private LocalDateTime fechaRespuesta;
}
