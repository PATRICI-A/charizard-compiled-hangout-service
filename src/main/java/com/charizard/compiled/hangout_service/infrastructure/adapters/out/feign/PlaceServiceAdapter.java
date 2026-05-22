package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import com.charizard.compiled.hangout_service.domain.ports.out.PlaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaceServiceAdapter implements PlaceServicePort {

    private final PlaceClient placeClient;

    @Override
    public List<PlaceResponse> getPlaces() {
        List<PlaceResponse> all = new ArrayList<>();
        all.addAll(placeClient.getBuildings());
        all.addAll(placeClient.getFoodOutlets());
        all.addAll(placeClient.getSportsAreas());
        return all;
    }
}
