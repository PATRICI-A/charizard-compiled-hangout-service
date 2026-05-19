package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryServicePort {
    List<CategoryResponse> getCategories();
}
