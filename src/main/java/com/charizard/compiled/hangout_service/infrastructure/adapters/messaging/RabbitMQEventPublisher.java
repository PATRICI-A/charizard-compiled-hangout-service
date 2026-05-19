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

    @Value("${rabbitmq.routing-key.invitation-rejected:invitation.rejected}")
    private String invitationRejectedKey;

    @Value("${rabbitmq.routing-key.parche-dissolved:parche.dissolved}")
    private String parcheDissolvedKey;

    @Value("${rabbitmq.routing-key.member-left:member.left}")
    private String memberLeftKey;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishParcheCreated(UUID parcheId, UUID ownerId,
                                     LocalDateTime scheduledAt, int totalParchesCreated) {
        ParcheCreatedMessage message = ParcheCreatedMessage.builder()
                .captainId(ownerId)
                .parcheId(parcheId)
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
                .invitationId(invitationId)
                .parcheId(parcheId)
                .studentId(studentId)
                .inviterId(inviterId)
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
                .invitationId(invitationId)
                .parcheId(parcheId)
                .invitedStudentId(invitedStudentId)
                .captainId(inviterId)
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, invitationSentKey, message);

        log.info("[RabbitMQ] Published invitation.sent → invitationId={} invitedStudentId={} parcheId={}",
                invitationId, invitedStudentId, parcheId);
    }

    @Override
    public void publishMemberJoined(UUID parcheId, String parcheNombre,
                                    UUID ownerId, UUID estudianteId) {
        MemberJoinedMessage message = MemberJoinedMessage.builder()
                .ownerId(ownerId)
                .estudianteId(estudianteId)
                .nombreParche(parcheNombre)
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, memberJoinedKey, message);

        log.info("[RabbitMQ] Published member.joined → estudianteId={} parcheId={}",
                estudianteId, parcheId);
    }

    @Override
    public void publishInvitationRejected(UUID invitationId, UUID parcheId,
                                          UUID invitedStudentId, UUID inviterId) {
        InvitationRejectedMessage message = InvitationRejectedMessage.builder()
                .invitationId(invitationId)
                .parcheId(parcheId)
                .invitedStudentId(invitedStudentId)
                .inviterId(inviterId)
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, invitationRejectedKey, message);

        log.info("[RabbitMQ] Published invitation.rejected → invitationId={} inviterId={}",
                invitationId, inviterId);
    }

    @Override
    public void publishParcheDissolved(UUID parcheId, String parcheNombre,
                                       java.util.List<UUID> memberIds) {
        ParcheDissolvedMessage message = ParcheDissolvedMessage.builder()
                .parcheId(parcheId)
                .parcheNombre(parcheNombre)
                .memberIds(memberIds)
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, parcheDissolvedKey, message);

        log.info("[RabbitMQ] Published parche.dissolved → parcheId={} members={}",
                parcheId, memberIds.size());
    }

    @Override
    public void publishMemberLeft(UUID parcheId, String parcheNombre,
                                  UUID studentId, java.util.List<UUID> memberIds) {
        MemberLeftMessage message = MemberLeftMessage.builder()
                .parcheId(parcheId)
                .parcheNombre(parcheNombre)
                .studentId(studentId)
                .memberIds(memberIds)
                .build();

        rabbitTemplate.convertAndSend(hangoutExchange, memberLeftKey, message);

        log.info("[RabbitMQ] Published member.left → studentId={} parcheId={}",
                studentId, parcheId);
    }
}
