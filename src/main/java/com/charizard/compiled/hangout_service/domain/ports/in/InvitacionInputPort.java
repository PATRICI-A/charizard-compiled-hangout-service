package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.EnviarInvitacionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada (Input Port) para los casos de uso de invitaciones.
 * Define el contrato que debe implementar el servicio de aplicación.
 */
public interface InvitacionInputPort {

    /**
     * Envía invitaciones a estudiantes para un parche privado.
     *
     * @param parcheId ID del parche
     * @param capitanId ID del capitán que envía las invitaciones
     * @param estudiantesIds lista de IDs de estudiantes a invitar
     * @return respuesta con invitaciones creadas y errores parciales
     */
    EnviarInvitacionResponse enviarInvitacion(UUID parcheId, UUID capitanId, List<UUID> estudiantesIds);
}
