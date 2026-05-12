package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de archivado manual de parches.
 * Permite al capitán cambiar el estado de un parche de ACTIVE a FILED.
 */
public interface CloseParcheInputPort {
    /**
     * Archiva un parche (soft delete). Solo el capitán puede ejecutar esta acción.
     *
     * @param id        ID del parche a archivar
     * @param captainId ID del capitán que solicita el archivado
     */
    void closeParche(UUID id, UUID captainId);
}
