package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheDetailResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
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
 * Búsqueda pública: solo PUBLIC + ACTIVE; filtros: nombre, fecha, categoria, cupoDisponible.
 * Detalle (/{id}): enriquecido con members, place y event via OpenFeign.
 * Mis parches: parches activos (PUBLIC o PRIVATE) donde el usuario es miembro.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetParcheUseCase implements GetParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheMapper parcheMapper;

    @Override
    public List<ParcheResponse> getParches(String nombre, LocalDate fecha, String categoria, Boolean cupoDisponible) {
        return parcheRepository.findByFilters(nombre, fecha, categoria).stream()
                .filter(p -> {
                    if (cupoDisponible == null) return true;
                    boolean hayEspacio = memberRepository.countByParcheId(p.getId()) < p.getMaximumQuota();
                    return cupoDisponible ? hayEspacio : !hayEspacio;
                })
                .map(p -> parcheMapper.toResponse(p, memberRepository.countByParcheId(p.getId())))
                .toList();
    }

    @Override
    public ParcheDetailResponse getParcheById(UUID id) {
        Parche parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));

        List<Member> members = memberRepository.findByParcheId(id);
        int memberCount = members.size();

        List<MemberResponse> memberResponses = members.stream()
                .map(m -> MemberResponse.builder()
                        .id(m.getId())
                        .parcheId(m.getParcheId())
                        .studentId(m.getStudentId())
                        .unionDate(m.getUnionDate())
                        .build())
                .toList();

        return ParcheDetailResponse.builder()
                .id(parche.getId())
                .name(parche.getName())
                .description(parche.getDescription())
                .category(parche.getCategory())
                .type(parche.getType())
                .status(parche.getStatus())
                .maximumQuota(parche.getMaximumQuota())
                .actualMembers(memberCount)
                .ownerId(parche.getOwnerId())
                .date(parche.getDate())
                .hour(parche.getHour())
                .imageUrl(parche.getImageUrl())
                .place(null)
                .event(null)
                .members(memberResponses)
                .build();
    }

    @Override
    public List<ParcheResponse> getMyParches(UUID userId) {
        List<UUID> parcheIds = memberRepository.findParcheIdsByStudentId(userId);
        return parcheRepository.findActiveByIds(parcheIds).stream()
                .map(p -> parcheMapper.toResponse(p, memberRepository.countByParcheId(p.getId())))
                .toList();
    }
}
