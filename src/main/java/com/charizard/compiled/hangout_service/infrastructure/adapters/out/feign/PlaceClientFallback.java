package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlaceClientFallback implements PlaceClient {

    @Override
    public List<PlaceResponse> getBuildings() {
        log.warn("[Feign fallback] geo-service unavailable — returning empty buildings list");
        return List.of();
    }

    @Override
    public List<PlaceResponse> getFoodOutlets() {
        log.warn("[Feign fallback] geo-service unavailable — returning empty food outlets list");
        return List.of();
    }

    @Override
    public List<PlaceResponse> getSportsAreas() {
        log.warn("[Feign fallback] geo-service unavailable — returning empty sports areas list");
        return List.of();
    }
}
