package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Feign client para el microservicio de Places.
 * Si la URL no está configurada o el servicio no está disponible,
 * el fallback {@link PlaceClientFallback} devuelve lista vacía / Optional.empty().
 */
@FeignClient(name = "place-service", url = "${services.places.url:}", fallback = PlaceClientFallback.class)
public interface PlaceClient {

    @GetMapping("/api/v1/places")
    List<PlaceResponse> getPlaces();

    @GetMapping("/api/v1/places/{placeId}")
    Optional<PlaceResponse> getPlaceById(@PathVariable UUID placeId);
}
