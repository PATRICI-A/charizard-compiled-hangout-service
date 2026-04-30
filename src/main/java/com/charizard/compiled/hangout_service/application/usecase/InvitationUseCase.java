package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.SendInvitationResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ErrorResponse;
import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitationInputPort;
import com.charizard.compiled.hangout_service.domain.events.InvitationSentEvent;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use case for the invitation flow.
 * Implements business logic and orchestrates output ports.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InvitationUseCase implements InvitationInputPort {

    private final InvitationRepositoryPort invitationRepository;
    private final MemberRepositoryPort memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public SendInvitationResponse sendInvitation(UUID parcheId, UUID captainId, List<UUID> studentIds) {
        List<Invitation> createdInvitations = new ArrayList<>();
        List<ErrorResponse> errors = new ArrayList<>();

        boolean isCaptain = memberRepository.existsByParcheIdAndStudentIdAndMemberRole(
                parcheId, captainId, MemberRole.CAPTAIN);

        if (!isCaptain) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no es capitán de este parche"
            );
        }

        for (UUID studentId : studentIds) {
            try {
                boolean isAlreadyMember = memberRepository.existsByParcheIdAndStudentId(parcheId, studentId);
                if (isAlreadyMember) {
                    throw new StudentAlreadyMemberException(
                            "El estudiante ya es miembro del parche");
                }

                var existingInvitation = invitationRepository
                        .findByParcheIdAndInvitedStudentId(parcheId, studentId);
                if (existingInvitation.isPresent() &&
                        existingInvitation.get().getStatus() == InvitationStatus.PENDING) {
                    throw new DuplicateInvitationException(
                            "Ya existe una invitación pendiente para este estudiante");
                }

                Invitation newInvitation = Invitation.builder()
                        .parcheId(parcheId)
                        .captainId(captainId)
                        .invitedStudentId(studentId)
                        .status(InvitationStatus.PENDING)
                        .build();

                Invitation saved = invitationRepository.save(newInvitation);
                createdInvitations.add(saved);

                eventPublisher.publishEvent(new InvitationSentEvent(
                        saved.getId(),
                        saved.getParcheId(),
                        saved.getInvitedStudentId(),
                        saved.getCaptainId()
                ));

            } catch (StudentAlreadyMemberException | DuplicateInvitationException e) {
                errors.add(ErrorResponse.builder()
                        .studentId(studentId)
                        .error(e.getMessage())
                        .status(409)
                        .build());
            } catch (Exception e) {
                errors.add(ErrorResponse.builder()
                        .studentId(studentId)
                        .error("Error interno: " + e.getMessage())
                        .status(400)
                        .build());
            }
        }

        return SendInvitationResponse.builder()
                .createdInvitations(createdInvitations.stream()
                        .map(this::toResponse)
                        .toList())
                .errors(errors)
                .build();
    }

    private InvitationResponse toResponse(Invitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .parcheId(invitation.getParcheId())
                .invitedStudentId(invitation.getInvitedStudentId())
                .status(invitation.getStatus())
                .sentAt(invitation.getSentAt())
                .build();
    }
}
