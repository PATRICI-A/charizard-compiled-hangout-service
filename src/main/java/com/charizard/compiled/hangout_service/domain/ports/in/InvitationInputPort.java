package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.SendInvitationResponse;

import java.util.List;
import java.util.UUID;

/**
 * Input port for invitation use cases.
 * Defines the contract the application service must implement.
 */
public interface InvitationInputPort {

    SendInvitationResponse sendInvitation(UUID parcheId, UUID captainId, List<UUID> studentIds);
}
