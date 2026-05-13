package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.InvitationAlreadyRespondedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.in.RespondInvitationInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.NotificacionPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso para responder invitaciones a parches privados.
 * El estudiante invitado puede aceptar o rechazar. Al aceptar,
 * se crea automáticamente la membresía y se publican eventos
 * de integración con otros servicios.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RespondInvitationUseCase implements RespondInvitationInputPort {

    private final InvitationRepositoryPort invitationRepository;
    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheEventPublisherPort parcheEventPublisher;
    private final NotificacionPort notificacionPort;

    @Override
    public InvitationResponse acceptInvitation(UUID invitationId, UUID studentId) {
        return respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED);
    }

    @Override
    public InvitationResponse rejectInvitation(UUID invitationId, UUID studentId) {
        return respondInvitation(invitationId, studentId, InvitationStatus.REJECTED);
    }

    @Override
    public InvitationResponse respondInvitation(UUID invitationId, UUID studentId, InvitationStatus answer) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        if (!invitation.getInvitedStudentId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the invited student");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyRespondedException();
        }

        if (answer == InvitationStatus.ACCEPTED) {
            Parche parche = parcheRepository.findById(invitation.getParcheId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hangout not found"));

            int currentMembers = memberRepository.countByParcheId(invitation.getParcheId());
            if (currentMembers >= parche.getMaximumQuota()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Hangout is already full");
            }

            if (memberRepository.countParchesActivosByStudentId(studentId) >= 5) {
                throw new MaxHangoutsReachedException();
            }

            Member newMember = Member.builder()
                    .parcheId(invitation.getParcheId())
                    .studentId(studentId)
                    .memberRole(MemberRole.STUDENT)
                    .build();
            memberRepository.save(newMember);

            notificacionPort.notificarNuevoMiembro(invitation.getCaptainId(), studentId, parche.getName());

            parcheEventPublisher.publishInvitationAccepted(
                    invitationId, invitation.getParcheId(), studentId, invitation.getCaptainId());

            parcheEventPublisher.publishMemberJoined(
                    invitation.getParcheId(), parche.getName(), invitation.getCaptainId(), studentId);
        }

        invitation.setStatus(answer);
        invitation.setRespondedAt(LocalDateTime.now());
        Invitation updated = invitationRepository.save(invitation);

        return toResponse(updated);
    }

    private InvitationResponse toResponse(Invitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .parcheId(invitation.getParcheId())
                .invitedStudentId(invitation.getInvitedStudentId())
                .status(invitation.getStatus())
                .sentAt(invitation.getSentAt())
                .respondedAt(invitation.getRespondedAt())
                .build();
    }
}
