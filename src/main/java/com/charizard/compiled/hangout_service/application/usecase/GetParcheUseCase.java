package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.in.GetParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para consultar parches.
 * Permite buscar parches por filtros (tipo, estado, nombre, fecha, cupo disponible)
 * y obtener un parche específico por su ID.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetParcheUseCase implements GetParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheMapper parcheMapper;

    @Override
    public List<ParcheResponse> getParches(ParcheType tipo, ParcheStatus estado, String nombre, LocalDate fecha, Boolean cupoDisponible) {
        return parcheRepository.findByFilters(tipo, estado, nombre, fecha).stream()
                .filter(p -> {
                    if (cupoDisponible == null) return true;
                    boolean hayEspacio = memberRepository.countByParcheId(p.getId()) < p.getMaximumQuota();
                    return cupoDisponible ? hayEspacio : !hayEspacio;
                })
                .map(p -> parcheMapper.toResponse(p, memberRepository.countByParcheId(p.getId())))
                .toList();
    }

    @Override
    public ParcheResponse getParcheById(UUID id) {
        Parche parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));
        return parcheMapper.toResponse(parche, memberRepository.countByParcheId(id));
    }
}
