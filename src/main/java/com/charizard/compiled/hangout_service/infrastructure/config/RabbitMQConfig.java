package com.charizard.compiled.hangout_service.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for hangout-service.
 *
 * Hangout publishes events to its own exchange (hangout.events).
 * Notification-service and Gamification-service bind their own queues to it.
 * Hangout does not consume any queues.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.hangout:hangout.events}")
    private String hangoutExchange;

    /**
     * Declares the hangout.events TopicExchange.
     * Durable=true so it survives broker restarts.
     * Auto-delete=false so it persists even when no consumers are bound.
     */
    @Bean
    public TopicExchange hangoutExchange() {
        return new TopicExchange(hangoutExchange, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
