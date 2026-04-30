package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;

import java.util.UUID;

public interface MemberRepositoryPort {
    boolean existsByParcheIdAndStudentId(UUID parcheId, UUID studentId);
    boolean existsByParcheIdAndStudentIdAndMemberRole(UUID parcheId, UUID studentId, MemberRole role);
    int countParchesActivosByStudentId(UUID studentId);
    int countByParcheId(UUID parcheId);
    Member save(Member member);
    void deleteByParcheIdAndStudentId(UUID parcheId, UUID studentId);
}
