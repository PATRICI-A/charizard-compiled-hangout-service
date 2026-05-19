package com.charizard.compiled.hangout_service.domain.ports.out;

import java.util.List;

/**
 * Puerto de salida para obtener categorías desde el microservicio de categorías.
 */
public interface CategoryServicePort {
    List<String> getCategories();
}
