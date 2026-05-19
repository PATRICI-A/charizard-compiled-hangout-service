package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

/**
 * Feign client para el microservicio de Eventos.
 */
@FeignClient(name = "event-service", url = "${services.events.url:}", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/api/v1/events/{eventId}")
    Optional<EventResponse> getEventById(@PathVariable UUID eventId);
}
