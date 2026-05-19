package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;
import com.charizard.compiled.hangout_service.domain.ports.out.CategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryServiceAdapter implements CategoryServicePort {

    private final CategoryClient categoryClient;

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryClient.getCategories();
    }
}
