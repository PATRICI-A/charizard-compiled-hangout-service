package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import com.charizard.compiled.hangout_service.domain.ports.out.NotificacionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adaptador que implementa {@link NotificacionPort} publicando eventos de dominio
 * {@link NuevoMiembroEvent} a través de {@link ApplicationEventPublisher}
 * para ser procesados de forma asíncrona por los listeners.
 */
@Slf4j
// @Component
@RequiredArgsConstructor
public class NotificacionAdapter implements NotificacionPort {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void notificarNuevoMiembro(UUID capitanId, UUID estudianteId, String nombreParche) {
        eventPublisher.publishEvent(new NuevoMiembroEvent(capitanId, estudianteId, nombreParche, LocalDateTime.now()));
        log.info("Notificación enviada al capitán {}: nuevo miembro {} en parche {}", capitanId, estudianteId, nombreParche);
    }
}
