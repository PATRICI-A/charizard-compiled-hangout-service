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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionAdapterTest {

    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks NotificacionAdapter adapter;

    @Test
    @DisplayName("notificarNuevoMiembro publica un NuevoMiembroEvent")
    void notificarNuevoMiembro_publicaNuevoMiembroEvent() {
        UUID capitanId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        adapter.notificarNuevoMiembro(capitanId, estudianteId, "Parche Fútbol");

        verify(eventPublisher).publishEvent(any(NuevoMiembroEvent.class));
    }

    @Test
    @DisplayName("notificarNuevoMiembro publica evento con los IDs y nombre de parche correctos")
    void notificarNuevoMiembro_publicaEventoConDatosCorrectos() {
        UUID capitanId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String nombreParche = "Parche Estudio";

        adapter.notificarNuevoMiembro(capitanId, estudianteId, nombreParche);

        verify(eventPublisher).publishEvent(argThat((Object event) -> {
            if (!(event instanceof NuevoMiembroEvent e)) return false;
            return e.capitanId().equals(capitanId) &&
                   e.estudianteId().equals(estudianteId) &&
                   e.nombreParche().equals(nombreParche);
        }));
    }
}
