package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link MemberEntity}.
 * Proporciona métodos para gestionar la membresía de estudiantes en parches,
 * incluyendo validaciones de existencia y conteos.
 */
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {

    int countByParcheId(UUID parcheId);

    boolean existsByParcheIdAndStudentId(UUID parcheId, UUID studentId);

    boolean existsByParcheIdAndStudentIdAndMemberRole(UUID parcheId, UUID studentId, MemberRole memberRole);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.studentId = :studentId " +
           "AND EXISTS (SELECT p FROM ParcheEntity p WHERE p.id = m.parcheId AND p.status = :status)")
    int countParchesActivosByStudentIdAndStatus(@Param("studentId") UUID studentId,
                                                @Param("status") ParcheStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM MemberEntity m WHERE m.parcheId = :parcheId AND m.studentId = :studentId")
    void deleteByParcheIdAndStudentId(@Param("parcheId") UUID parcheId,
                                      @Param("studentId") UUID studentId);
}
