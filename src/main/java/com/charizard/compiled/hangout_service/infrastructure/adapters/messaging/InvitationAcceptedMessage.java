package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RabbitMQ payload for the invitation.accepted routing key.
 * Consumers: gamification-service, notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationAcceptedMessage {

    private UUID invitationId;
    private UUID parcheId;
    private UUID studentId;
    private UUID inviterId;
    private LocalDateTime occurredAt;
}
