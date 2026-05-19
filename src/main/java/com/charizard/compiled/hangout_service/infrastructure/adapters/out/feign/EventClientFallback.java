package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Fallback del cliente Feign para Eventos.
 */
@Slf4j
@Component
public class EventClientFallback implements EventClient {

    @Override
    public Optional<EventResponse> getEventById(UUID eventId) {
        log.warn("[Feign fallback] event-service not available for eventId={} — returning empty", eventId);
        return Optional.empty();
    }
}
