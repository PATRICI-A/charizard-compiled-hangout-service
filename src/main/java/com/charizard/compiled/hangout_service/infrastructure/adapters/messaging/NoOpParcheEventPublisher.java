package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;

import java.util.UUID;

public class NoOpParcheEventPublisher implements ParcheEventPublisherPort {

    @Override
    public void publishInvitationAccepted(UUID invitationId, UUID parcheId, UUID studentId, UUID captainId) {}

    @Override
    public void publishInvitationSent(UUID invitationId, UUID parcheId, UUID invitedStudentId, UUID captainId) {}

    @Override
    public void publishMemberJoined(UUID parcheId, String parcheNombre, UUID capitanId, UUID estudianteId) {}
}
