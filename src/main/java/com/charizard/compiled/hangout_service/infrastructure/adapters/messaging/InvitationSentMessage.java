package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payload de RabbitMQ para la routing key {@code invitation.sent}.
 * Serializado como JSON. Consumido por: notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationSentMessage {

    private String invitationId;
    private String parcheId;
    private String invitedStudentId;
    private String captainId;
    private LocalDateTime occurredAt;
}
