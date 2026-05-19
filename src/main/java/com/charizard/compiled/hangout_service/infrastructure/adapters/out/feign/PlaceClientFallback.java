package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fallback del cliente Feign para Places.
 * Devuelve lista vacía cuando el servicio no está disponible.
 */
@Slf4j
@Component
public class PlaceClientFallback implements PlaceClient {

    @Override
    public List<PlaceResponse> getPlaces() {
        log.warn("[Feign fallback] place-service not available — returning empty list");
        return List.of();
    }
}
