package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de creación de parches.
 * Define el contrato que debe implementar {@link com.charizard.compiled.hangout_service.application.usecase.CreateParcheUseCase}.
 */
public interface CreateParcheInputPort {
    /**
     * Crea un nuevo parche y registra al creador como capitán.
     *
     * @param request   datos del parche a crear
     * @param captainId ID del estudiante que crea el parche
     * @return respuesta con los datos del parche creado
     */
    ParcheResponse createParche(CreateParcheRequest request, UUID captainId);
}
