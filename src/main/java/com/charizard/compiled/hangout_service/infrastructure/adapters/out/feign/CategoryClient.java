package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Feign client para el microservicio de Categorías.
 */
@FeignClient(name = "category-service", url = "${services.categories.url:}", fallback = CategoryClientFallback.class)
public interface CategoryClient {

    @GetMapping("/api/v1/categories")
    List<String> getCategories();
}
