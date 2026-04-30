package com.charizard.compiled.hangout_service.domain.ports.out;

import java.util.UUID;

public interface NotificacionPushPort {
    void enviarNotificacion(UUID destinatarioId, String titulo, String cuerpo);
}
