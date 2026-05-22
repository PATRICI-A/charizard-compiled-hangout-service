package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;
import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;
import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.GetOpcionesInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.CategoryServicePort;
import com.charizard.compiled.hangout_service.domain.ports.out.EventServicePort;
import com.charizard.compiled.hangout_service.domain.ports.out.PlaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para obtener las opciones disponibles al crear o editar un parche:
 * categorías, lugares y eventos (via OpenFeign con fallback a lista vacía).
 */
@Service
@RequiredArgsConstructor
public class GetOpcionesUseCase implements GetOpcionesInputPort {

    private final CategoryServicePort categoryServicePort;
    private final PlaceServicePort placeServicePort;
    private final EventServicePort eventServicePort;

    @Override
    public List<CategoryResponse> getCategorias() {
        return categoryServicePort.getCategories();
    }

    @Override
    public List<PlaceResponse> getLugares() {
        return placeServicePort.getPlaces();
    }

    @Override
    public List<EventResponse> getEventos() {
        return eventServicePort.getEventos();
    }
}
