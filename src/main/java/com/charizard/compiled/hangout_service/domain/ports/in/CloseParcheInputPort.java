package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de archivado manual de parches.
 * Solo un administrador (ROLE_ADMIN) puede archivar manualmente un parche.
 */
public interface CloseParcheInputPort {
    /**
     * Archiva un parche (soft delete). Solo ROLE_ADMIN puede ejecutar esta acción.
     * La verificación de rol se hace en el controlador via @PreAuthorize.
     *
     * @param id ID del parche a archivar
     */
    void closeParche(UUID id);
}
