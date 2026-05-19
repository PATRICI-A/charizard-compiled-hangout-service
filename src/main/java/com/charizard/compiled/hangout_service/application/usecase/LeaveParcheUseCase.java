package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.LeaveParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para que un estudiante abandone un parche voluntariamente.
 * Si el caller es el owner:
 *   - Si es el único miembro: el parche se archiva (FILED).
 *   - Si hay más miembros: newOwnerId es obligatorio y debe ser un miembro actual.
 *     El ownership se transfiere y el owner abandona el parche.
 * Si el caller no es el owner: simplemente sale del parche.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LeaveParcheUseCase implements LeaveParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheEventPublisherPort parcheEventPublisher;

    @Override
    public void salirDeParche(UUID parcheId, UUID studentId, UUID newOwnerId) {
        Parche parche = parcheRepository.findById(parcheId)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + parcheId));

        if (!memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student is not a member of this parche");
        }

        if (parche.getStatus() == ParcheStatus.FILED) {
            throw new IllegalArgumentException("Cannot leave an archived parche");
        }

        boolean isOwner = parche.getOwnerId().equals(studentId);

        if (isOwner) {
            int memberCount = memberRepository.countByParcheId(parcheId);
            if (memberCount == 1) {
                // Único miembro: archivar el parche (no quedan miembros a quienes notificar)
                parche.setStatus(ParcheStatus.FILED);
                parcheRepository.save(parche);
                memberRepository.deleteByParcheIdAndStudentId(parcheId, studentId);
            } else {
                // Hay más miembros: newOwnerId es obligatorio
                if (newOwnerId == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "newOwnerId is required when the owner leaves a parche with other members");
                }
                if (!memberRepository.existsByParcheIdAndStudentId(parcheId, newOwnerId)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The new owner must already be a member of this parche");
                }
                parche.setOwnerId(newOwnerId);
                parcheRepository.save(parche);
                memberRepository.deleteByParcheIdAndStudentId(parcheId, studentId);

                List<UUID> remainingMemberIds = memberRepository.findByParcheId(parcheId).stream()
                        .map(Member::getStudentId)
                        .toList();
                parcheEventPublisher.publishMemberLeft(parcheId, parche.getName(), studentId, remainingMemberIds);
            }
        } else {
            memberRepository.deleteByParcheIdAndStudentId(parcheId, studentId);

            List<UUID> remainingMemberIds = memberRepository.findByParcheId(parcheId).stream()
                    .map(Member::getStudentId)
                    .toList();
            parcheEventPublisher.publishMemberLeft(parcheId, parche.getName(), studentId, remainingMemberIds);
        }
    }
}
