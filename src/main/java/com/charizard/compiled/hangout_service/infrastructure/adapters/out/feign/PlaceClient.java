package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Feign client para el microservicio de Places.
 * Si la URL no está configurada o el servicio no está disponible,
 * el fallback {@link PlaceClientFallback} devuelve una lista vacía.
 */
@FeignClient(name = "place-service", url = "${services.places.url:}", fallback = PlaceClientFallback.class)
public interface PlaceClient {

    @GetMapping("/api/v1/places")
    List<PlaceResponse> getPlaces();
}
