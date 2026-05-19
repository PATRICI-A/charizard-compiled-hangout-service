package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * RabbitMQ payload for the parche.dissolved routing key.
 * Consumed by: notification-service (notifies all members).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcheDissolvedMessage {

    private UUID parcheId;
    private String parcheNombre;
    private List<UUID> memberIds;
}
