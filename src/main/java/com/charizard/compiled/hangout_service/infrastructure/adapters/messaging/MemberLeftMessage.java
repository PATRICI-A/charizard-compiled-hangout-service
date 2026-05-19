package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * RabbitMQ payload for the member.left routing key.
 * Consumed by: notification-service (notifies all remaining members).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLeftMessage {

    private UUID parcheId;
    private String parcheNombre;
    private UUID studentId;
    private List<UUID> memberIds;
}
