package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de limpieza de usuario eliminado.
 * Elimina al usuario de todos los parches activos en los que participa,
 * transfiere ownership cuando corresponde, y cancela sus invitaciones pendientes.
 */
public interface CleanupUserInputPort {

    /**
     * Limpia toda la presencia del usuario en el sistema de parches.
     *
     * @param userId ID del usuario que fue eliminado del sistema
     */
    void cleanupUser(UUID userId);
}
