package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.MemberEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepositoryPort {

    private final MemberRepository memberRepository;
    private final MemberEntityMapper mapper;

    @Override
    public boolean existsByParcheIdAndStudentId(UUID parcheId, UUID studentId) {
        return memberRepository.existsByParcheIdAndStudentId(parcheId, studentId);
    }

    @Override
    public boolean existsByParcheIdAndStudentIdAndMemberRole(UUID parcheId, UUID studentId, MemberRole role) {
        return memberRepository.existsByParcheIdAndStudentIdAndMemberRole(parcheId, studentId, role);
    }

    @Override
    public int countParchesActivosByStudentId(UUID studentId) {
        return memberRepository.countParchesActivosByStudentIdAndStatus(studentId, ParcheStatus.ACTIVE);
    }

    @Override
    public int countByParcheId(UUID parcheId) {
        return memberRepository.countByParcheId(parcheId);
    }

    @Override
    public Member save(Member member) {
        return mapper.toDomain(memberRepository.save(mapper.toEntity(member)));
    }
}
