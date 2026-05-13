package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionEventListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("handle procesa NuevoMiembroEvent y publica en RabbitMQ")
    void handle_procesaEvento_publicaEnRabbitMQ() {
        var listener = new NotificacionEventListener(rabbitTemplate);
        NuevoMiembroEvent event = new NuevoMiembroEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Parche de Prueba",
                LocalDateTime.now()
        );

        assertThatCode(() -> listener.handle(event)).doesNotThrowAnyException();

        verify(rabbitTemplate).convertAndSend(any(), any(), any(Object.class));
    }

    @Test
    @DisplayName("handle acepta eventos con nombre de parche vacío sin lanzar excepción")
    void handle_nombreParche_vacio_sinExcepcion() {
        var listener = new NotificacionEventListener(rabbitTemplate);
        NuevoMiembroEvent event = new NuevoMiembroEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "",
                LocalDateTime.now()
        );

        assertThatCode(() -> listener.handle(event)).doesNotThrowAnyException();

        verify(rabbitTemplate).convertAndSend(any(), any(), any(Object.class));
    }
}
