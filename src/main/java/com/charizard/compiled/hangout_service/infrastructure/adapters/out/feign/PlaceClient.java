package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "geo-service", url = "${services.geo.url:}", fallback = PlaceClientFallback.class)
public interface PlaceClient {

    @GetMapping("/api/v1/geo/zones/buildings")
    List<PlaceResponse> getBuildings();

    @GetMapping("/api/v1/geo/zones/food")
    List<PlaceResponse> getFoodOutlets();

    @GetMapping("/api/v1/geo/zones/sports")
    List<PlaceResponse> getSportsAreas();
}
