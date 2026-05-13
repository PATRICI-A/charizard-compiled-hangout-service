package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de salida de un parche.
 * Un estudiante puede abandonar voluntariamente un parche del que es miembro.
 */
public interface LeaveParcheInputPort {
    /**
     * Elimina la membresía de un estudiante en un parche.
     *
     * @param parcheId  ID del parche del que salir
     * @param studentId ID del estudiante que abandona
     */
    void salirDeParche(UUID parcheId, UUID studentId);
}
