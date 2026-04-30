package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepositoryPort {

    private final MemberRepository memberRepository;

    @Override
    public boolean existsByParcheIdAndStudentId(UUID parcheId, UUID studentId) {
        return memberRepository.existsByParcheIdAndStudentId(parcheId, studentId);
    }

    @Override
    public boolean existsByParcheIdAndStudentIdAndMemberRole(UUID parcheId, UUID studentId, MemberRole role) {
        return memberRepository.existsByParcheIdAndStudentIdAndMemberRole(parcheId, studentId, role);
    }

}
