package com.charizard.compiled.hangout_service.domain.ports.out;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Output port for publishing hangout domain events to external systems
 * (e.g., RabbitMQ).
 *
 * Keeps the use-case layer decoupled from the messaging technology.
 */
public interface ParcheEventPublisherPort {

    /**
     * Publishes the fact that a user created a new parche (they become the owner).
     * Consumed by: Gamification service (Primer Parche, Anfitrión, Planificador badges).
     *
     * @param parcheId            ID of the newly created parche
     * @param ownerId             ID of the owner who created it
     * @param scheduledAt         date+time the parche is scheduled for (may be null)
     * @param totalParchesCreated total parches this owner has created (including this one)
     */
    void publishParcheCreated(UUID parcheId, UUID ownerId,
                              LocalDateTime scheduledAt, int totalParchesCreated);

    /**
     * Publishes the fact that a student accepted an invitation to a parche.
     * Consumed by: Gamification service, Notification service.
     *
     * @param inviterId ID of the member who sent the invitation
     */
    void publishInvitationAccepted(UUID invitationId, UUID parcheId,
                                   UUID studentId, UUID inviterId);

    /**
     * Publishes the fact that a member sent an invitation to a student.
     * Consumed by: Notification service.
     *
     * @param inviterId ID of the member who sent the invitation
     */
    void publishInvitationSent(UUID invitationId, UUID parcheId,
                               UUID invitedStudentId, UUID inviterId);

    /**
     * Publishes the fact that a student joined a parche (directly or via invitation).
     * Consumed by: Gamification service (Primer Parche, Imán Social), Notification service.
     *
     * @param ownerId ID of the current owner of the parche
     */
    void publishMemberJoined(UUID parcheId, String parcheNombre,
                             UUID ownerId, UUID estudianteId);
}
