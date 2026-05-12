package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de respuesta a invitaciones.
 * El estudiante invitado puede aceptar o rechazar una invitación pendiente.
 */
public interface RespondInvitationInputPort {

    /**
     * Responde una invitación con el estado indicado (ACCEPTED o REJECTED).
     *
     * @param invitationId ID de la invitación a responder
     * @param studentId    ID del estudiante invitado
     * @param answer       respuesta (ACCEPTED / REJECTED)
     * @return datos de la invitación actualizada
     */
    InvitationResponse respondInvitation(UUID invitationId, UUID studentId, InvitationStatus answer);

    /**
     * Acepta una invitación y crea automáticamente la membresía.
     *
     * @param invitationId ID de la invitación a aceptar
     * @param studentId    ID del estudiante invitado
     * @return datos de la invitación aceptada
     */
    InvitationResponse acceptInvitation(UUID invitationId, UUID studentId);

    /**
     * Rechaza una invitación.
     *
     * @param invitationId ID de la invitación a rechazar
     * @param studentId    ID del estudiante invitado
     * @return datos de la invitación rechazada
     */
    InvitationResponse rejectInvitation(UUID invitationId, UUID studentId);
}
