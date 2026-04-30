package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;

import java.util.UUID;

public interface RespondInvitationInputPort {

    InvitationResponse acceptInvitation(UUID invitationId, UUID studentId);

    InvitationResponse rejectInvitation(UUID invitationId, UUID studentId);
}
