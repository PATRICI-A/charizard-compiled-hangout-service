package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for the invitation repository.
 * Defines persistence operations without exposing technical details.
 */
public interface InvitationRepositoryPort {
    Invitation save(Invitation invitation);

    Optional<Invitation> findByParcheIdAndInvitedStudentId(UUID parcheId, UUID studentId);

    List<Invitation> findByInvitedStudentIdAndStatus(UUID studentId, InvitationStatus status);

    List<Invitation> findByParcheId(UUID parcheId);

    Optional<Invitation> findById(UUID invitationId);
}
