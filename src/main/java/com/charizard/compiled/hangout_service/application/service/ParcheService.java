package com.charizard.compiled.hangout_service.application.service;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParcheService {

    private final ParcheRepository parcheRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ParcheResponse crearParche(CreateParcheRequest req, UUID captainId) {
        ParcheEntity parche = ParcheEntity.builder()
                .name(req.getName())
                .description(req.getDescription())
                .place(req.getPlace())
                .type(req.getType())
                .maximumQuota(req.getMaximumQuota())
                .dateRealization(LocalDateTime.of(req.getDate(), req.getHour()))
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .eventId(req.getEventId())
                .build();

        ParcheEntity saved = parcheRepository.save(parche);

        memberRepository.save(MemberEntity.builder()
                .parche(saved)
                .studentId(captainId)
                .memberRole(MemberRole.CAPTAIN)
                .build());

        log.info("Parche created: {} by captain: {}", saved.getId(), captainId);
        return toResponse(saved, 1);
    }

    @Transactional(readOnly = true)
    public List<ParcheResponse> obtenerParches(ParcheType tipo, ParcheStatus estado) {
        List<ParcheEntity> parches;

        if (tipo != null && estado != null) {
            parches = parcheRepository.findByTypeAndStatus(tipo, estado);
        } else if (tipo != null) {
            parches = parcheRepository.findByType(tipo);
        } else if (estado != null) {
            parches = parcheRepository.findByStatus(estado);
        } else {
            parches = parcheRepository.findAll();
        }

        return parches.stream()
                .map(p -> toResponse(p, memberRepository.countByParcheId(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ParcheResponse obtenerParchePorId(UUID id) {
        ParcheEntity parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));
        return toResponse(parche, memberRepository.countByParcheId(id));
    }

    @Transactional
    public ParcheResponse actualizarParche(UUID id, UpdateParcheRequest req, UUID solicitanteId) {
        ParcheEntity parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));

        if (!parche.getCaptainId().equals(solicitanteId)) {
            throw new AccessDeniedException("Only the captain can edit this parche");
        }

        if (req.getName() != null)         parche.setName(req.getName());
        if (req.getDescription() != null)  parche.setDescription(req.getDescription());
        if (req.getPlace() != null)        parche.setPlace(req.getPlace());
        if (req.getType() != null)         parche.setType(req.getType());
        if (req.getMaximumQuota() != null) parche.setMaximumQuota(req.getMaximumQuota());
        if (req.getEventId() != null)      parche.setEventId(req.getEventId());

        if (req.getDate() != null || req.getHour() != null) {
            LocalDate date = req.getDate() != null
                    ? req.getDate()
                    : parche.getDateRealization().toLocalDate();
            LocalTime hour = req.getHour() != null
                    ? req.getHour()
                    : parche.getDateRealization().toLocalTime();
            parche.setDateRealization(LocalDateTime.of(date, hour));
        }

        ParcheEntity updated = parcheRepository.save(parche);
        log.info("Parche updated: {}", id);
        return toResponse(updated, memberRepository.countByParcheId(id));
    }

    @Transactional
    public void eliminarParche(UUID id, UUID captainId) {
        ParcheEntity parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));

        if (!parche.getCaptainId().equals(captainId)) {
            throw new AccessDeniedException("Only the captain can delete this parche");
        }

        parche.setStatus(ParcheStatus.FILED);
        parcheRepository.save(parche);
        log.info("Parche archived (soft-delete): {}", id);
    }

    @Transactional(readOnly = true)
    public boolean validarCupoDisponible(UUID parcheId) {
        ParcheEntity parche = parcheRepository.findById(parcheId)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + parcheId));
        int currentMembers = memberRepository.countByParcheId(parcheId);
        return currentMembers < parche.getMaximumQuota();
    }

    private ParcheResponse toResponse(ParcheEntity entity, int memberCount) {
        return ParcheResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .place(entity.getPlace())
                .type(entity.getType())
                .status(entity.getStatus())
                .maximumQuota(entity.getMaximumQuota())
                .actualMembers(memberCount)
                .captainId(entity.getCaptainId())
                .dateRealization(entity.getDateRealization())
                .build();
    }
}
