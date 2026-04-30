package com.charizard.compiled.hangout_service.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class InvitationAcceptedEvent {

    private final UUID invitationId;
    private final UUID parcheId;
    private final UUID studentId;
    private final UUID captainId;
}
