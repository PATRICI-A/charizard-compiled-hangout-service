package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * RabbitMQ payload for the invitation.sent routing key.
 * Consumed by: notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationSentMessage {

    private UUID invitationId;
    private UUID parcheId;
    private UUID invitedStudentId;
    private UUID captainId;
}
