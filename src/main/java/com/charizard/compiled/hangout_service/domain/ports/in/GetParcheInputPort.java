package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para los casos de uso de consulta de parches.
 * Proporciona métodos para listar con filtros y buscar por ID.
 */
public interface GetParcheInputPort {
    /**
     * Busca parches aplicando filtros opcionales.
     *
     * @param tipo           filtro por tipo (PUBLIC / PRIVATE)
     * @param estado         filtro por estado (ACTIVE / FILED)
     * @param nombre         filtro por nombre (búsqueda parcial, case-insensitive)
     * @param fecha          filtro por fecha (yyyy-MM-dd)
     * @param cupoDisponible filtro por disponibilidad de cupo (true = hay espacio)
     * @return lista de parches que coinciden con los filtros
     */
    List<ParcheResponse> getParches(ParcheType tipo, ParcheStatus estado, String nombre, LocalDate fecha, Boolean cupoDisponible);
    /**
     * Busca un parche por su ID.
     *
     * @param id identificador único del parche
     * @return datos del parche encontrado
     */
    ParcheResponse getParcheById(UUID id);
}
