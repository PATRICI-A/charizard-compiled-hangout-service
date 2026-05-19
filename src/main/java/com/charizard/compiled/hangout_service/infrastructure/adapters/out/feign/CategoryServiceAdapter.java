package com.charizard.compiled.hangout_service.infrastructure.adapters.out.feign;

import com.charizard.compiled.hangout_service.domain.ports.out.CategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador que implementa {@link CategoryServicePort} delegando en {@link CategoryClient}.
 */
@Component
@RequiredArgsConstructor
public class CategoryServiceAdapter implements CategoryServicePort {

    private final CategoryClient categoryClient;

    @Override
    public List<String> getCategories() {
        return categoryClient.getCategories();
    }
}
