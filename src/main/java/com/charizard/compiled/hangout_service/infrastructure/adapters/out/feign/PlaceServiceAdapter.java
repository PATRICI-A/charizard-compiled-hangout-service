package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import com.charizard.compiled.hangout_service.domain.ports.out.PlaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador que implementa {@link PlaceServicePort} delegando en {@link PlaceClient}.
 */
@Component
@RequiredArgsConstructor
public class PlaceServiceAdapter implements PlaceServicePort {

    private final PlaceClient placeClient;

    @Override
    public List<PlaceResponse> getPlaces() {
        return placeClient.getPlaces();
    }

    @Override
    public Optional<PlaceResponse> getPlaceById(UUID placeId) {
        return placeClient.getPlaceById(placeId);
    }
}
