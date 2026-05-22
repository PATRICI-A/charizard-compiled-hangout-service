package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para obtener lugares desde el microservicio de Places.
 */
public interface PlaceServicePort {
    List<PlaceResponse> getPlaces();
    Optional<PlaceResponse> getPlaceById(UUID placeId);
}
