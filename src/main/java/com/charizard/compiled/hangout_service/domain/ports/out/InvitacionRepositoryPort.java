package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Invitacion;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para el repositorio de invitaciones.
 * Define las operaciones de persistencia sin exponer detalles técnicos.
 */
public interface InvitacionRepositoryPort {
    /**
     * Guarda una invitación en el repositorio.
     *
     * @param invitacion la invitación a guardar
     * @return la invitación guardada con su ID generado
     */
    Invitacion save(Invitacion invitacion);

    /**
     * Busca una invitación por parche y estudiante invitado.
     *
     * @param parcheId ID del parche
     * @param estudianteId ID del estudiante
     * @return la invitación si existe
     */
    Optional<Invitacion> findByParcheIdAndEstudianteInvitadoId(UUID parcheId, UUID estudianteId);

    /**
     * Busca invitaciones por estudiante y estado.
     *
     * @param estudianteId ID del estudiante
     * @param estado estado de la invitación
     * @return lista de invitaciones que cumplen los criterios
     */
    List<Invitacion> findByEstudianteInvitadoIdAndEstado(UUID estudianteId, EstadoInvitacion estado);

    /**
     * Busca todas las invitaciones de un parche.
     *
     * @param parcheId ID del parche
     * @return lista de invitaciones del parche
     */
    List<Invitacion> findByParcheId(UUID parcheId);
}
