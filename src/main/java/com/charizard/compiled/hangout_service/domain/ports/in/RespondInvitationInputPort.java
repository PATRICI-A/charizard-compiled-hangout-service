package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;

import java.util.UUID;

public interface RespondInvitationInputPort {

    InvitationResponse respondInvitation(UUID invitationId, UUID studentId, InvitationStatus answer);
}
