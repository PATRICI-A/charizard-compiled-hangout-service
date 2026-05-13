package com.charizard.compiled.hangout_service.domain.events;

import java.util.UUID;

/**
 * Evento de dominio publicado cuando un capitán envía una invitación
 * a un estudiante para unirse a un parche privado.
 */
public class InvitationSentEvent {
    /** ID de la invitación creada */
    private final UUID invitationId;
    /** ID del parche al que se invita */
    private final UUID parcheId;
    /** ID del estudiante invitado */
    private final UUID invitedStudentId;
    /** ID del capitán que envía la invitación */
    private final UUID captainId;

    public InvitationSentEvent(UUID invitationId, UUID parcheId, UUID invitedStudentId, UUID captainId) {
        this.invitationId = invitationId;
        this.parcheId = parcheId;
        this.invitedStudentId = invitedStudentId;
        this.captainId = captainId;
    }

    public UUID getInvitationId() { return invitationId; }
    public UUID getParcheId() { return parcheId; }
    public UUID getInvitedStudentId() { return invitedStudentId; }
    public UUID getCaptainId() { return captainId; }
}
