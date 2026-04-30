package com.charizard.compiled.hangout_service.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class InvitacionAceptadaEvent {

    private final UUID invitacionId;
    private final UUID parcheId;
    private final UUID estudianteId;
    private final UUID capitanId;
}
