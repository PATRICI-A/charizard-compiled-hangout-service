package com.charizard.compiled.hangout_service.domain.ports.out;

import java.util.UUID;

/**
 * Puerto de salida para notificaciones internas del microservicio.
 * Permite notificar al capitán cuando un nuevo estudiante se une a su parche.
 */
public interface NotificacionPort {
    /**
     * Notifica al capitán que un nuevo estudiante se unió a su parche.
     *
     * @param capitanId    ID del capitán del parche
     * @param estudianteId ID del estudiante que se unió
     * @param nombreParche nombre del parche al que se unió
     */
    void notificarNuevoMiembro(UUID capitanId, UUID estudianteId, String nombreParche);
}
