package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de invitaciones.
 * Define las operaciones que el dominio necesita para gestionar
 * el ciclo de vida de las invitaciones a parches privados.
 */
public interface InvitationRepositoryPort {
    /** Guarda o actualiza una invitación */
    Invitation save(Invitation invitation);
    /** Busca una invitación por parche y estudiante invitado */
    Optional<Invitation> findByParcheIdAndInvitedStudentId(UUID parcheId, UUID studentId);
    /** Busca invitaciones de un estudiante por estado */
    List<Invitation> findByInvitedStudentIdAndStatus(UUID studentId, InvitationStatus status);
    /** Busca todas las invitaciones de un parche */
    List<Invitation> findByParcheId(UUID parcheId);
    /** Busca una invitación por su ID */
    Optional<Invitation> findById(UUID invitationId);
}
