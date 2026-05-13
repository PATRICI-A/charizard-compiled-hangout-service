package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de unión a un parche público.
 * Un estudiante puede unirse directamente sin necesidad de invitación.
 */
public interface JoinParcheInputPort {
    /**
     * Registra a un estudiante como miembro de un parche público.
     *
     * @param parcheId  ID del parche al que unirse
     * @param studentId ID del estudiante que se une
     * @return datos de la membresía creada
     */
    MemberResponse unirseAParche(UUID parcheId, UUID studentId);
}
