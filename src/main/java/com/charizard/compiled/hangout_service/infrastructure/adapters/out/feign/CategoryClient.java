package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "category-service", url = "${services.categories.url:}", fallback = CategoryClientFallback.class)
public interface CategoryClient {

    @GetMapping("/api/v1/categories/all")
    List<CategoryResponse> getCategories();
}
