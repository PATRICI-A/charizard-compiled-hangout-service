package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de salida de un parche.
 * Un estudiante puede abandonar voluntariamente un parche del que es miembro.
 * Si es el owner, debe transferir el ownership antes de salir.
 */
public interface LeaveParcheInputPort {
    /**
     * Elimina la membresía de un estudiante en un parche.
     * Si el estudiante es el owner, newOwnerId es obligatorio (salvo que sea el único miembro,
     * en cuyo caso el parche queda archivado).
     *
     * @param parcheId    ID del parche del que salir
     * @param studentId   ID del estudiante que abandona
     * @param newOwnerId  ID del nuevo owner (requerido solo si el caller es el owner actual)
     */
    void salirDeParche(UUID parcheId, UUID studentId, UUID newOwnerId);
}
