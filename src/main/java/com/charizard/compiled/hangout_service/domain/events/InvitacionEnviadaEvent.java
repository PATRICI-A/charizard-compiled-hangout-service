package com.charizard.compiled.hangout_service.domain.events;

import java.util.UUID;

public class InvitacionEnviadaEvent {
    private final UUID invitacionId;
    private final UUID parcheId;
    private final UUID estudianteInvitadoId;
    private final UUID capitanId;

    public InvitacionEnviadaEvent(UUID invitacionId, UUID parcheId, UUID estudianteInvitadoId, UUID capitanId) {
        this.invitacionId = invitacionId;
        this.parcheId = parcheId;
        this.estudianteInvitadoId = estudianteInvitadoId;
        this.capitanId = capitanId;
    }

    public UUID getInvitacionId() { return invitacionId; }
    public UUID getParcheId() { return parcheId; }
    public UUID getEstudianteInvitadoId() { return estudianteInvitadoId; }
    public UUID getCapitanId() { return capitanId; }
}
