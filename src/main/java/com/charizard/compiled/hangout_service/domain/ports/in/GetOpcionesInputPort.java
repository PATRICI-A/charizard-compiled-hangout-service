package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;
import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;
import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;

import java.util.List;

/**
 * Puerto de entrada para obtener las opciones disponibles
 * al crear o editar un parche: categorías, lugares y eventos.
 */
public interface GetOpcionesInputPort {

    List<CategoryResponse> getCategorias();

    List<PlaceResponse> getLugares();

    List<EventResponse> getEventos();
}
