package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitationInputPort;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Caso de uso para enviar invitaciones a parches privados.
 * El capitán puede invitar estudiantes; valida que no sean ya miembros
 * y que no tengan una invitación pendiente duplicada. Publica eventos
 * de dominio para notificaciones.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InvitationUseCase implements InvitationInputPort {

    private final InvitationRepositoryPort invitationRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheRepositoryPort parcheRepository;
    private final ParcheEventPublisherPort parcheEventPublisher;

    @Override
    public InvitationResponse sendInvitation(UUID parcheId, UUID captainId, UUID studentId) {
        var parche = parcheRepository.findById(parcheId)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + parcheId));

        if (!parche.getCaptainId().equals(captainId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the captain of this hangout");
        }

        if (memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)) {
            throw new StudentAlreadyMemberException("Student is already a member of this hangout");
        }

        var existingInvitation = invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId);
        if (existingInvitation.isPresent() && existingInvitation.get().getStatus() == InvitationStatus.PENDING) {
            throw new DuplicateInvitationException("A pending invitation already exists for this student");
        }

        Invitation newInvitation = Invitation.builder()
                .parcheId(parcheId)
                .captainId(captainId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        Invitation saved = invitationRepository.save(newInvitation);

        parcheEventPublisher.publishInvitationSent(
                saved.getId(),
                saved.getParcheId(),
                saved.getInvitedStudentId(),
                saved.getCaptainId()
        );

        return toResponse(saved);
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
