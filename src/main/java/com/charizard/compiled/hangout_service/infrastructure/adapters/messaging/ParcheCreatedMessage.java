package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RabbitMQ payload for the parche.created routing key on hangout.events exchange.
 *
 * Consumed by: Gamification service (Primer Parche, Anfitrión, Planificador badges).
 *
 * Field names are read by GamificationEventListener.onParcheCreated(), which maps:
 *   captainId            → BadgeUnlockEventRequest.userId
 *   totalParchesCreated  → BadgeUnlockEventRequest.totalParchesCreated
 *   parcheScheduledAt    → BadgeUnlockEventRequest.parcheScheduledAt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcheCreatedMessage {

    /** UUID of the captain who created the parche. */
    private String captainId;

    /** UUID of the newly created parche. */
    private String parcheId;

    /**
     * Date and time the parche is scheduled for.
     * Used to evaluate Planificador badge (>3 days ahead).
     * May be null if the parche has no scheduled date/time.
     */
    private LocalDateTime parcheScheduledAt;

    /**
     * Total number of parches this captain has created (including this one).
     * Used to evaluate Anfitrión badge (≥2 parches created).
     */
    private int totalParchesCreated;
}
