package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionAdapterTest {

    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks NotificacionAdapter adapter;

    @Test
    @DisplayName("notificarNuevoMiembro publica NuevoMiembroEvent")
    void notificarNuevoMiembro_publicaEvento() {
        UUID capitanId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        adapter.notificarNuevoMiembro(capitanId, estudianteId, "Parche Fútbol");

        verify(eventPublisher).publishEvent(any(NuevoMiembroEvent.class));
    }
}
