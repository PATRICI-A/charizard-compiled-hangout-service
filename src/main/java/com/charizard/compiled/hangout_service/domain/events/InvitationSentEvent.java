package com.charizard.compiled.hangout_service.domain.events;

import java.util.UUID;

public class InvitationSentEvent {
    private final UUID invitationId;
    private final UUID parcheId;
    private final UUID invitedStudentId;
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
