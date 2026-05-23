package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link InvitationEntity}.
 * Proporciona métodos para gestionar invitaciones, incluyendo
 * la cancelación masiva de invitaciones pendientes de un parche.
 */
@Repository
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {

    Optional<InvitationEntity> findByParcheIdAndInvitedStudentId(UUID parcheId, UUID studentId);

    List<InvitationEntity> findByInvitedStudentIdAndStatus(UUID studentId, InvitationStatus status);

    List<InvitationEntity> findByParcheId(UUID parcheId);

    @Modifying
    @Transactional
    @Query("UPDATE InvitationEntity i SET i.status = :rejected WHERE i.parcheId = :parcheId AND i.status = :pending")
    void cancelPendingByParcheId(@Param("parcheId") UUID parcheId,
                                 @Param("pending") InvitationStatus pending,
                                 @Param("rejected") InvitationStatus rejected);

    default void cancelPendingByParcheId(UUID parcheId) {
        cancelPendingByParcheId(parcheId, InvitationStatus.PENDING, InvitationStatus.REJECTED);
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM InvitationEntity i WHERE i.invitedStudentId = :userId OR i.inviterId = :userId")
    void deleteByInvitedStudentIdOrInviterId(@Param("userId") UUID userId);
}
