package com.charizard.compiled.hangout_service.infrastructure.adapters.notification;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import com.charizard.compiled.hangout_service.infrastructure.adapters.messaging.MemberJoinedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
// @Component
public class NotificacionEventListener {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.hangout}")
    private String hangoutExchange;

    @Value("${rabbitmq.routing-key.member-joined}")
    private String memberJoinedKey;

    public NotificacionEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    @EventListener
    public void handle(NuevoMiembroEvent event) {
        log.info("[NOTIFICACION] Capitán: {} | Nuevo miembro: {} | Parche: {}",
                event.capitanId(), event.estudianteId(), event.nombreParche());

        MemberJoinedMessage message = MemberJoinedMessage.builder()
                .ownerId(event.capitanId())
                .estudianteId(event.estudianteId())
                .nombreParche(event.nombreParche())
                .timestamp(event.timestamp())
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, memberJoinedKey, message);

        log.info("[RabbitMQ] Published member.joined (from NotificacionEventListener) → estudianteId={} parche={}",
                event.estudianteId(), event.nombreParche());
    }
}
