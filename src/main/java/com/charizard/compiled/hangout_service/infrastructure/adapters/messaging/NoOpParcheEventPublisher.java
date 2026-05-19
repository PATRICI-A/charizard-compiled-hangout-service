package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * No-op fallback for {@link ParcheEventPublisherPort}.
 * Active only when no other implementation is registered (e.g., local dev without RabbitMQ).
 * All methods are intentional no-ops — no events are published.
 */
@Component
@ConditionalOnMissingBean(ParcheEventPublisherPort.class)
public class NoOpParcheEventPublisher implements ParcheEventPublisherPort {

    @Override
    public void publishParcheCreated(UUID parcheId, UUID ownerId,
                                     LocalDateTime scheduledAt, int totalParchesCreated) {}

    @Override
    public void publishInvitationAccepted(UUID invitationId, UUID parcheId, UUID studentId, UUID inviterId) {}

    @Override
    public void publishInvitationSent(UUID invitationId, UUID parcheId, UUID invitedStudentId, UUID inviterId) {}

    @Override
    public void publishMemberJoined(UUID parcheId, String parcheNombre, UUID ownerId, UUID estudianteId) {}

    @Override
    public void publishInvitationRejected(UUID invitationId, UUID parcheId,
                                          UUID invitedStudentId, UUID inviterId) {}

    @Override
    public void publishParcheDissolved(UUID parcheId, String parcheNombre,
                                       java.util.List<UUID> memberIds) {}

    @Override
    public void publishMemberLeft(UUID parcheId, String parcheNombre,
                                  UUID studentId, java.util.List<UUID> memberIds) {}
}
