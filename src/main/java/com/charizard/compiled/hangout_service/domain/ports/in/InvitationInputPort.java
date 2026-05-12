package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de envío de invitaciones.
 * El capitán de un parche privado puede invitar a estudiantes específicos.
 */
public interface InvitationInputPort {

    /**
     * Envía una invitación a un estudiante para unirse a un parche privado.
     *
     * @param parcheId  ID del parche privado
     * @param captainId ID del capitán que envía la invitación
     * @param studentId ID del estudiante invitado
     * @return datos de la invitación creada
     */
    InvitationResponse sendInvitation(UUID parcheId, UUID captainId, UUID studentId);
}
