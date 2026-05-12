package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de actualización de parches.
 * Permite modificar parcialmente los datos de un parche existente.
 */
public interface UpdateParcheInputPort {
    /**
     * Actualiza los datos de un parche (solo el capitán puede hacerlo).
     *
     * @param id            ID del parche a actualizar
     * @param req           datos a modificar (campos opcionales)
     * @param solicitanteId ID del estudiante que solicita la actualización
     * @return parche actualizado
     */
    ParcheResponse updateParche(UUID id, UpdateParcheRequest req, UUID solicitanteId);
}
