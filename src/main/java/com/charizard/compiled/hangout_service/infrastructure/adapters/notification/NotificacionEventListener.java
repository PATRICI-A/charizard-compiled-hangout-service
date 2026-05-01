package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listens for NuevoMiembroEvent and logs the notification.
 * Prepared for future integration with WebSocket or Firebase Cloud Messaging
 * via NotificacionPushPort.
 */
@Slf4j
@Component
public class NotificacionEventListener {

    @Async
    @EventListener
    public void handle(NuevoMiembroEvent event) {
        log.info("[NOTIFICACION] Capitán: {} | Nuevo miembro: {} | Parche: {}",
                event.capitanId(), event.estudianteId(), event.nombreParche());
    }
}
