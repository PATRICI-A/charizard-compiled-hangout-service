package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {

    @Query("SELECT COUNT(m) > 0 FROM MemberEntity m WHERE m.parche.id = :parcheId AND m.studentId = :studentId")
    boolean existsByParcheIdAndStudentId(@Param("parcheId") UUID parcheId, @Param("studentId") UUID studentId);

    @Query("SELECT COUNT(m) > 0 FROM MemberEntity m WHERE m.parche.id = :parcheId AND m.studentId = :studentId AND m.memberRole = :role")
    boolean existsByParcheIdAndStudentIdAndMemberRole(@Param("parcheId") UUID parcheId,
                                                       @Param("studentId") UUID studentId,
                                                       @Param("role") MemberRole role);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.studentId = :studentId AND m.parche.status = :status")
    int countParchesActivosByStudentIdAndStatus(@Param("studentId") UUID studentId,
                                                @Param("status") ParcheStatus status);

    int countByParcheId(UUID parcheId);
}
