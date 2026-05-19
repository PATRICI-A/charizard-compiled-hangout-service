package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class RabbitMQEventPublisher implements ParcheEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.hangout:hangout.events}")
    private String hangoutExchange;

    @Value("${rabbitmq.routing-key.parche-created:parche.created}")
    private String parcheCreatedKey;

    @Value("${rabbitmq.routing-key.invitation-accepted:invitation.accepted}")
    private String invitationAcceptedKey;

    @Value("${rabbitmq.routing-key.invitation-sent:invitation.sent}")
    private String invitationSentKey;

    @Value("${rabbitmq.routing-key.member-joined:member.joined}")
    private String memberJoinedKey;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishParcheCreated(UUID parcheId, UUID ownerId,
                                     LocalDateTime scheduledAt, int totalParchesCreated) {
        ParcheCreatedMessage message = ParcheCreatedMessage.builder()
                .captainId(ownerId.toString())
                .parcheId(parcheId.toString())
                .parcheScheduledAt(scheduledAt)
                .totalParchesCreated(totalParchesCreated)
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, parcheCreatedKey, message);

        log.info("[RabbitMQ] Published parche.created → ownerId={} parcheId={} totalCreated={}",
                ownerId, parcheId, totalParchesCreated);
    }

    @Override
    public void publishInvitationAccepted(UUID invitationId, UUID parcheId,
                                          UUID studentId, UUID inviterId) {
        InvitationAcceptedMessage message = InvitationAcceptedMessage.builder()
                .invitationId(invitationId.toString())
                .parcheId(parcheId.toString())
                .studentId(studentId.toString())
                .inviterId(inviterId.toString())
                .occurredAt(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, invitationAcceptedKey, message);

        log.info("[RabbitMQ] Published invitation.accepted → invitationId={} studentId={} parcheId={}",
                invitationId, studentId, parcheId);
    }

    @Override
    public void publishInvitationSent(UUID invitationId, UUID parcheId,
                                      UUID invitedStudentId, UUID inviterId) {
        InvitationSentMessage message = InvitationSentMessage.builder()
                .invitationId(invitationId.toString())
                .parcheId(parcheId.toString())
                .invitedStudentId(invitedStudentId.toString())
                .captainId(inviterId.toString())
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, invitationSentKey, message);

        log.info("[RabbitMQ] Published invitation.sent → invitationId={} invitedStudentId={} parcheId={}",
                invitationId, invitedStudentId, parcheId);
    }

    @Override
    public void publishMemberJoined(UUID parcheId, String parcheNombre,
                                    UUID ownerId, UUID estudianteId) {
        MemberJoinedMessage message = MemberJoinedMessage.builder()
                .ownerId(ownerId.toString())
                .estudianteId(estudianteId.toString())
                .nombreParche(parcheNombre)
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, memberJoinedKey, message);

        log.info("[RabbitMQ] Published member.joined → estudianteId={} parcheId={}",
                estudianteId, parcheId);
    }
}
