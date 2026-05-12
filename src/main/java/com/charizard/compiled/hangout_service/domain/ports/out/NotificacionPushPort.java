package com.charizard.compiled.hangout_service.domain.ports.out;

import java.util.UUID;

/**
 * Puerto de salida para notificaciones push.
 * Interfaz preparada para futura integración con servicios de notificaciones
 * push como Firebase Cloud Messaging o WebSockets.
 */
public interface NotificacionPushPort {
    /**
     * Envía una notificación push a un estudiante.
     *
     * @param destinatarioId ID del estudiante destino
     * @param titulo         título de la notificación
     * @param cuerpo         cuerpo del mensaje
     */
    void enviarNotificacion(UUID destinatarioId, String titulo, String cuerpo);
}
