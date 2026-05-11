package com.charizard.compiled.hangout_service.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ topology for hangout-service.
 *
 * Exchange : hangout.events  (topic, durable)
 *
 * Events published:
 *   invitation.accepted  → gamification.invitation.accepted  (Gamification service)
 *   invitation.accepted  → notification.invitation.accepted  (Notification service M05)
 */
@Configuration
public class RabbitMQConfig {

    // ── Exchange ─────────────────────────────────────────────────────────────
    public static final String EXCHANGE = "hangout.events";

    // ── Routing keys ─────────────────────────────────────────────────────────
    public static final String RK_INVITATION_ACCEPTED = "invitation.accepted";

    // ── Queues ────────────────────────────────────────────────────────────────
    public static final String QUEUE_GAMIFICATION  = "gamification.invitation.accepted";
    public static final String QUEUE_NOTIFICATION  = "notification.invitation.accepted";

    // ── Exchange bean ─────────────────────────────────────────────────────────
    @Bean
    public TopicExchange hangoutEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // ── Queue beans ───────────────────────────────────────────────────────────
    @Bean
    public Queue gamificationInvitationAcceptedQueue() {
        return QueueBuilder.durable(QUEUE_GAMIFICATION).build();
    }

    @Bean
    public Queue notificationInvitationAcceptedQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICATION).build();
    }

    // ── Bindings ──────────────────────────────────────────────────────────────
    @Bean
    public Binding gamificationBinding(Queue gamificationInvitationAcceptedQueue,
                                       TopicExchange hangoutEventsExchange) {
        return BindingBuilder
                .bind(gamificationInvitationAcceptedQueue)
                .to(hangoutEventsExchange)
                .with(RK_INVITATION_ACCEPTED);
    }

    @Bean
    public Binding notificationBinding(Queue notificationInvitationAcceptedQueue,
                                       TopicExchange hangoutEventsExchange) {
        return BindingBuilder
                .bind(notificationInvitationAcceptedQueue)
                .to(hangoutEventsExchange)
                .with(RK_INVITATION_ACCEPTED);
    }

    // ── Serialization ─────────────────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
