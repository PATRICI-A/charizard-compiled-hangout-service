package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.ports.in.UpdateParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateParcheUseCase implements UpdateParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheMapper parcheMapper;

    @Override
    public ParcheResponse updateParche(UUID id, UpdateParcheRequest req, UUID solicitanteId) {
        Parche parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));

        if (!parche.getCaptainId().equals(solicitanteId)) {
            throw new AccessDeniedException("Only the captain can edit this parche");
        }

        if (req.getName() != null)         parche.setName(req.getName());
        if (req.getDescription() != null)  parche.setDescription(req.getDescription());
        if (req.getPlace() != null)        parche.setPlace(req.getPlace());
        if (req.getCategory() != null)     parche.setCategory(req.getCategory());
        if (req.getType() != null)         parche.setType(req.getType());
        if (req.getMaximumQuota() != null) parche.setMaximumQuota(req.getMaximumQuota());
        if (req.getEventId() != null)      parche.setEventId(req.getEventId());
        if (req.getDate() != null)         parche.setDate(req.getDate());
        if (req.getHour() != null)         parche.setHour(req.getHour());

        Parche updated = parcheRepository.save(parche);
        return parcheMapper.toResponse(updated, memberRepository.countByParcheId(id));
    }
}
