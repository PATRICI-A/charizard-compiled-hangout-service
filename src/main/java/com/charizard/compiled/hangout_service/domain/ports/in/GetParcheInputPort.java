package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.ParcheDetailResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para los casos de uso de consulta de parches.
 * Proporciona métodos para listar con filtros y buscar por ID.
 */
public interface GetParcheInputPort {
    /**
     * Busca parches PUBLIC + ACTIVE aplicando filtros opcionales.
     *
     * @param nombre         filtro por nombre (búsqueda parcial, case-insensitive)
     * @param fecha          filtro por fecha (yyyy-MM-dd)
     * @param categoria      filtro por categoría
     * @param cupoDisponible filtro por disponibilidad de cupo (true = hay espacio)
     * @return lista de parches que coinciden con los filtros
     */
    List<ParcheResponse> getParches(String nombre, LocalDate fecha, String categoria, Boolean cupoDisponible);

    /**
     * Busca un parche por su ID, enriquecido con members, place y event.
     *
     * @param id identificador único del parche
     * @return datos detallados del parche
     */
    ParcheDetailResponse getParcheById(UUID id);

    /**
     * Retorna los parches activos (PUBLIC o PRIVATE) donde el usuario es miembro.
     *
     * @param userId ID del usuario autenticado
     * @return lista de parches activos del usuario
     */
    List<ParcheResponse> getMyParches(UUID userId);
}
