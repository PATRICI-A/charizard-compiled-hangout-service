package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.ports.out.NotificacionPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NoOpNotificacionAdapter implements NotificacionPort {

    @Override
    public void notificarNuevoMiembro(UUID capitanId, UUID estudianteId, String nombreParche) {}
}
