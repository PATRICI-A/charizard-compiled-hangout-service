package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.InvitationEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa {@link com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort}
 * usando Spring Data JPA. Traduce entre el modelo de dominio {@link com.charizard.compiled.hangout_service.domain.model.Invitation}
 * y la entidad JPA {@link InvitationEntity}.
 */
@Component
@RequiredArgsConstructor
public class InvitationRepositoryAdapter implements InvitationRepositoryPort {

    private final InvitationRepository invitationRepository;
    private final InvitationEntityMapper mapper;

    @Override
    public Invitation save(Invitation invitation) {
        InvitationEntity entity = mapper.toEntity(invitation);
        InvitationEntity saved = invitationRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Invitation> findByParcheIdAndInvitedStudentId(UUID parcheId, UUID studentId) {
        return invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Invitation> findByInvitedStudentIdAndStatus(UUID studentId, InvitationStatus status) {
        return invitationRepository.findByInvitedStudentIdAndStatus(studentId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findByParcheId(UUID parcheId) {
        return invitationRepository.findByParcheId(parcheId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Invitation> findById(UUID invitationId) {
        return invitationRepository.findById(invitationId).map(mapper::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        invitationRepository.deleteByInvitedStudentIdOrInviterId(userId);
    }
}
