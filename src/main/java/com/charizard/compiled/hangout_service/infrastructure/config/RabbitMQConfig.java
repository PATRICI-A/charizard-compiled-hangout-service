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
 * Configuración de la topología RabbitMQ para el hangout-service.
 *
 * Exchange : {@code hangout.events} (topic, durable)
 *
 * Eventos publicados:
 * <ul>
 *   <li>{@code invitation.accepted} → gamification / notification</li>
 *   <li>{@code invitation.sent} → notification</li>
 *   <li>{@code member.joined} → notification</li>
 * </ul>
 *
 * Colas:
 * <ul>
 *   <li>{@code gamification.invitation.accepted}</li>
 *   <li>{@code notification.invitation.accepted}</li>
 *   <li>{@code notification.invitation.sent}</li>
 *   <li>{@code notification.member.joined}</li>
 * </ul>
 */
@Configuration
public class RabbitMQConfig {

    // ── Exchange ─────────────────────────────────────────────────────────────
    public static final String EXCHANGE = "hangout.events";

    // ── Routing keys ─────────────────────────────────────────────────────────
    public static final String RK_INVITATION_ACCEPTED = "invitation.accepted";
    public static final String RK_INVITATION_SENT     = "invitation.sent";
    public static final String RK_MEMBER_JOINED       = "member.joined";

    // ── Queues ────────────────────────────────────────────────────────────────
    public static final String QUEUE_GAMIFICATION              = "gamification.invitation.accepted";
    public static final String QUEUE_NOTIFICATION_ACCEPTED     = "notification.invitation.accepted";
    public static final String QUEUE_NOTIFICATION_SENT         = "notification.invitation.sent";
    public static final String QUEUE_NOTIFICATION_MEMBER       = "notification.member.joined";

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
        return QueueBuilder.durable(QUEUE_NOTIFICATION_ACCEPTED).build();
    }

    @Bean
    public Queue notificationInvitationSentQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICATION_SENT).build();
    }

    @Bean
    public Queue notificationMemberJoinedQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICATION_MEMBER).build();
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
    public Binding notificationAcceptedBinding(Queue notificationInvitationAcceptedQueue,
                                               TopicExchange hangoutEventsExchange) {
        return BindingBuilder
                .bind(notificationInvitationAcceptedQueue)
                .to(hangoutEventsExchange)
                .with(RK_INVITATION_ACCEPTED);
    }

    @Bean
    public Binding notificationSentBinding(Queue notificationInvitationSentQueue,
                                           TopicExchange hangoutEventsExchange) {
        return BindingBuilder
                .bind(notificationInvitationSentQueue)
                .to(hangoutEventsExchange)
                .with(RK_INVITATION_SENT);
    }

    @Bean
    public Binding notificationMemberJoinedBinding(Queue notificationMemberJoinedQueue,
                                                   TopicExchange hangoutEventsExchange) {
        return BindingBuilder
                .bind(notificationMemberJoinedQueue)
                .to(hangoutEventsExchange)
                .with(RK_MEMBER_JOINED);
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
