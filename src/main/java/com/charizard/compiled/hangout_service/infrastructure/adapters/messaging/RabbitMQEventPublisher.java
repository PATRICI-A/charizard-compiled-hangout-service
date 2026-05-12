package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import com.charizard.compiled.hangout_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Publishes hangout domain events to RabbitMQ.
 *
 * Implements {@link ParcheEventPublisherPort} so the application layer
 * never depends directly on RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements ParcheEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishInvitationAccepted(UUID invitationId, UUID parcheId,
                                          UUID studentId, UUID captainId) {
        InvitationAcceptedMessage message = InvitationAcceptedMessage.builder()
                .invitationId(invitationId.toString())
                .parcheId(parcheId.toString())
                .studentId(studentId.toString())
                .captainId(captainId.toString())
                .ocurredAt(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_INVITATION_ACCEPTED,
                message
        );

        log.info("[RabbitMQ] Published invitation.accepted → invitationId={} studentId={} parcheId={}",
                invitationId, studentId, parcheId);
    }

    @Override
    public void publishInvitationSent(UUID invitationId, UUID parcheId,
                                      UUID invitedStudentId, UUID captainId) {
        InvitationSentMessage message = InvitationSentMessage.builder()
                .invitationId(invitationId.toString())
                .parcheId(parcheId.toString())
                .invitedStudentId(invitedStudentId.toString())
                .capatinId(captainId.toString())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_INVITATION_SENT,
                message
        );

        log.info("[RabbitMQ] Published invitation.sent → invitationId={} invitedStudentId={} parcheId={}",
                invitationId, invitedStudentId, parcheId);
    }

    @Override
    public void publishMemberJoined(UUID parcheId, String parcheNombre,
                                    UUID capitanId, UUID estudianteId) {
        MemberJoinedMessage message = MemberJoinedMessage.builder()
                .capitanId(capitanId.toString())
                .estudianteId(estudianteId.toString())
                .nombreParche(parcheNombre)
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_MEMBER_JOINED,
                message
        );

        log.info("[RabbitMQ] Published member.joined → estudianteId={} parcheId={}",
                estudianteId, parcheId);
    }
}
