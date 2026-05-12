package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RabbitMQ payload for the invitation.sent routing key.
 *
 * Field names match notification-service's InvitationSentEventDto exactly
 * (including the typo "capatinId").
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationSentMessage {

    private String invitationId;
    private String parcheId;
    private String invitedStudentId;
    private String capatinId;   // matches notification-service typo (captainId)
}
