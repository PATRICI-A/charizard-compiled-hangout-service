package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CategoryClientFallback implements CategoryClient {

    @Override
    public List<CategoryResponse> getCategories() {
        log.warn("[Feign fallback] category-service not available — returning empty list");
        return List.of();
    }
}
