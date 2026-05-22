package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;
import com.charizard.compiled.hangout_service.domain.ports.out.EventServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador que implementa {@link EventServicePort} delegando en {@link EventClient}.
 */
@Component
@RequiredArgsConstructor
public class EventServiceAdapter implements EventServicePort {

    private final EventClient eventClient;

    @Override
    public List<EventResponse> getEventos() {
        return eventClient.getEventos();
    }

    @Override
    public Optional<EventResponse> getEventById(UUID eventId) {
        return eventClient.getEventById(eventId);
    }
}
