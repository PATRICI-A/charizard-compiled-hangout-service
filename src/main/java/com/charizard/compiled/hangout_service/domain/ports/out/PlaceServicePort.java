package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;

import java.util.List;

public interface PlaceServicePort {
    List<PlaceResponse> getPlaces();
}
