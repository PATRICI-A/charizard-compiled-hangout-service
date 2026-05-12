package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RabbitMQ payload for the member.joined routing key.
 *
 * Field names match notification-service's MemberJoinedEventDto exactly.
 * Note: notification-service declares "nombreParche" as UUID (likely a typo on their side —
 * should be String). Jackson will deserialize our String value into their field regardless.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinedMessage {

    private String capitanId;
    private String estudianteId;
    private String nombreParche;
    private LocalDateTime timestamp;
}
