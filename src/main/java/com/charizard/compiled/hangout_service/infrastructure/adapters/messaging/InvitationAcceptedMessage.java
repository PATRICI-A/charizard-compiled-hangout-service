package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RabbitMQ payload for the invitation.accepted routing key.
 *
 * Field names match notification-service's InvitationAcceptedEventDto exactly
 * (including the typo "ocurredAt").
 * Consumers: gamification-service, notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationAcceptedMessage {

    private String invitationId;
    private String parcheId;
    private String studentId;
    private String captainId;
    private LocalDateTime occurredAt;
}
