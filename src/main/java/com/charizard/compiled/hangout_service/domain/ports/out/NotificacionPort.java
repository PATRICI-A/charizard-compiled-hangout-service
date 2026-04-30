package com.charizard.compiled.hangout_service.domain.ports.out;

import java.util.UUID;

public interface NotificacionPort {
    void notificarNuevoMiembro(UUID capitanId, UUID estudianteId, String nombreParche);
}
