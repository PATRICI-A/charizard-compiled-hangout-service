package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {

    Optional<InvitationEntity> findByParcheIdAndInvitedStudentId(UUID parcheId, UUID studentId);

    List<InvitationEntity> findByInvitedStudentIdAndStatus(UUID studentId, InvitationStatus status);

    List<InvitationEntity> findByParcheId(UUID parcheId);
}
