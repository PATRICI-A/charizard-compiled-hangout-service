package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.ports.in.CreateParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso para crear un nuevo parche.
 * Valida que el estudiante no exceda el límite de 5 parches activos,
 * persiste el parche y registra al creador como primer miembro (owner).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateParcheUseCase implements CreateParcheInputPort {

    private static final int MAX_ACTIVE_HANGOUTS = 5;

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheMapper parcheMapper;
    private final ParcheEventPublisherPort parcheEventPublisher;

    @Override
    public ParcheResponse createParche(CreateParcheRequest request, UUID ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("User not authenticated: X-User-Id header is required");
        }
        if (memberRepository.countParchesActivosByStudentId(ownerId) >= MAX_ACTIVE_HANGOUTS) {
            throw new MaxHangoutsReachedException();
        }

        Parche saved = parcheRepository.save(parcheMapper.toDomain(request, ownerId));

        memberRepository.save(Member.builder()
                .parcheId(saved.getId())
                .studentId(ownerId)
                .unionDate(LocalDateTime.now())
                .build());

        // totalParchesCreated = número de parches en que el usuario es owner (captainId en BD)
        int totalCreated = memberRepository.countByParcheId(saved.getId());

        LocalDateTime scheduledAt = (saved.getDate() != null && saved.getHour() != null)
                ? LocalDateTime.of(saved.getDate(), saved.getHour())
                : null;

        parcheEventPublisher.publishParcheCreated(saved.getId(), ownerId, scheduledAt, totalCreated);

        return parcheMapper.toResponse(saved, 1);
    }
}
