package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fallback del cliente Feign para Categorías.
 */
@Slf4j
@Component
public class CategoryClientFallback implements CategoryClient {

    @Override
    public List<String> getCategories() {
        log.warn("[Feign fallback] category-service not available — returning empty list");
        return List.of();
    }
}
