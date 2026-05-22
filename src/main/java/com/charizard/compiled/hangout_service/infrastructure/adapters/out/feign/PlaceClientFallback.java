package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Fallback del cliente Feign para Places.
 * Devuelve lista vacía / Optional.empty() cuando el servicio no está disponible.
 */
@Slf4j
@Component
public class PlaceClientFallback implements PlaceClient {

    @Override
    public List<PlaceResponse> getPlaces() {
        log.warn("[Feign fallback] place-service not available — returning empty list");
        return List.of();
    }

    @Override
    public Optional<PlaceResponse> getPlaceById(UUID placeId) {
        log.warn("[Feign fallback] place-service not available for placeId={} — returning empty", placeId);
        return Optional.empty();
    }
}
