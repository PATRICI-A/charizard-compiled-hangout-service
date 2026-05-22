package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para obtener eventos desde el microservicio de Eventos.
 */
public interface EventServicePort {
    List<EventResponse> getEventos();
    Optional<EventResponse> getEventById(UUID eventId);
}
