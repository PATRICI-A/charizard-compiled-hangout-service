package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

class NotificacionEventListenerTest {

    private final NotificacionEventListener listener = new NotificacionEventListener();

    @Test
    @DisplayName("handle procesa NuevoMiembroEvent sin lanzar excepción")
    void handle_procesaEvento_sinExcepcion() {
        NuevoMiembroEvent event = new NuevoMiembroEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Parche de Prueba",
                LocalDateTime.now()
        );

        assertThatCode(() -> listener.handle(event)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("handle acepta eventos con nombre de parche vacío sin lanzar excepción")
    void handle_nombreParche_vacio_sinExcepcion() {
        NuevoMiembroEvent event = new NuevoMiembroEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "",
                LocalDateTime.now()
        );

        assertThatCode(() -> listener.handle(event)).doesNotThrowAnyException();
    }
}
