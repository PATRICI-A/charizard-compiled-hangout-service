package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.MemberEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Adaptador que implementa {@link MemberRepositoryPort} usando Spring Data JPA.
 * Traduce entre el modelo de dominio {@link Member} y la entidad JPA {@link MemberEntity}
 * a través de {@link MemberEntityMapper}.
 */
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
    public int countParchesActivosByStudentId(UUID studentId) {
        return memberRepository.countParchesActivosByStudentIdAndStatus(studentId, ParcheStatus.ACTIVE);
    }

    @Override
    public int countByParcheId(UUID parcheId) {
        return memberRepository.countByParcheId(parcheId);
    }

    @Override
    public List<Member> findByParcheId(UUID parcheId) {
        return memberRepository.findByParcheId(parcheId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<UUID> findParcheIdsByStudentId(UUID studentId) {
        return memberRepository.findParcheIdsByStudentId(studentId);
    }

    @Override
    public Member save(Member member) {
        return mapper.toDomain(memberRepository.save(mapper.toEntity(member)));
    }

    @Override
    public void deleteByParcheIdAndStudentId(UUID parcheId, UUID studentId) {
        memberRepository.deleteByParcheIdAndStudentId(parcheId, studentId);
    }
}
