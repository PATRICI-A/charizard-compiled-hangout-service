package com.charizard.compiled.hangout_service.domain.ports.out;

import java.util.UUID;

/**
 * Output port for publishing hangout domain events to external systems
 * (e.g., RabbitMQ).
 *
 * Keeps the use-case layer decoupled from the messaging technology.
 */
public interface ParcheEventPublisherPort {

    /**
     * Publishes the fact that a student accepted an invitation to a parche.
     * Consumed by: Gamification service, Notification service.
     */
    void publishInvitationAccepted(UUID invitationId, UUID parcheId,
                                   UUID studentId, UUID captainId);

    /**
     * Publishes the fact that the captain sent an invitation to a student.
     * Consumed by: Notification service.
     */
    void publishInvitationSent(UUID invitationId, UUID parcheId,
                               UUID invitedStudentId, UUID captainId);

    /**
     * Publishes the fact that a student joined a parche (directly or via invitation).
     * Consumed by: Notification service.
     */
    void publishMemberJoined(UUID parcheId, String parcheNombre,
                             UUID capitanId, UUID estudianteId);
}
