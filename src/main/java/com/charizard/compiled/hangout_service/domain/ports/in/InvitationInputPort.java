package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de envío de invitaciones.
 * Cualquier miembro del parche puede invitar a estudiantes.
 */
public interface InvitationInputPort {

    /**
     * Envía una invitación a un estudiante para unirse a un parche.
     *
     * @param parcheId  ID del parche
     * @param inviterId ID del miembro que envía la invitación
     * @param studentId ID del estudiante invitado
     * @return datos de la invitación creada
     */
    InvitationResponse sendInvitation(UUID parcheId, UUID inviterId, UUID studentId);
}
