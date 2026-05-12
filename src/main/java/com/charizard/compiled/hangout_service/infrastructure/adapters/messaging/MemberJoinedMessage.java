package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payload de RabbitMQ para la routing key {@code member.joined}.
 * Serializado como JSON. Consumido por: notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinedMessage {

    private String parcheId;
    private String parcheNombre;
    private String capitanId;
    private String estudianteId;
    private LocalDateTime occurredAt;
}
