package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RabbitMQ payload for the member.joined routing key.
 * Consumed by: notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinedMessage {

    private UUID ownerId;
    private UUID estudianteId;
    private String nombreParche;
    private LocalDateTime timestamp;
}
