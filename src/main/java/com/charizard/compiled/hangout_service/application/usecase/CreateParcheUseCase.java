package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.in.CreateParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateParcheUseCase implements CreateParcheInputPort {

    private static final int MAX_ACTIVE_HANGOUTS = 5;

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheMapper parcheMapper;

    @Override
    public ParcheResponse createParche(CreateParcheRequest request, UUID captainId) {
        if (memberRepository.countParchesActivosByStudentId(captainId) >= MAX_ACTIVE_HANGOUTS) {
            throw new MaxHangoutsReachedException();
        }

        Parche saved = parcheRepository.save(parcheMapper.toDomain(request, captainId));

        memberRepository.save(Member.builder()
                .parcheId(saved.getId())
                .studentId(captainId)
                .unionDate(LocalDateTime.now())
                .memberRole(MemberRole.CAPTAIN)
                .build());

        return parcheMapper.toResponse(saved, 1);
    }
}
