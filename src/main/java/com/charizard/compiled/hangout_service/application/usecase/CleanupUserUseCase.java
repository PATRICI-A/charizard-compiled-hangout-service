package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.CleanupUserInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso que limpia la presencia de un usuario eliminado en el sistema de parches.
 *
 * <p>Para cada parche activo donde el usuario es miembro:
 * <ul>
 *   <li>Si NO es owner: lo elimina directamente de la membresía.</li>
 *   <li>Si es owner y es el único miembro: archiva el parche (FILED).</li>
 *   <li>Si es owner y hay más miembros: transfiere el ownership al miembro
 *       con mayor antigüedad (unionDate más antiguo) y luego lo elimina.</li>
 * </ul>
 * Al final elimina todas las invitaciones en las que el usuario participó,
 * tanto como invitado como invitador.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CleanupUserUseCase implements CleanupUserInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final InvitationRepositoryPort invitationRepository;

    @Override
    public void cleanupUser(UUID userId) {
        log.info("Iniciando cleanup del usuario {}", userId);

        List<UUID> parcheIds = memberRepository.findParcheIdsByStudentId(userId);
        log.info("Usuario {} encontrado en {} parches", userId, parcheIds.size());

        for (UUID parcheId : parcheIds) {
            parcheRepository.findById(parcheId).ifPresent(parche -> processParche(parche, userId));
        }

        invitationRepository.deleteByUserId(userId);
        log.info("Cleanup del usuario {} completado", userId);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void processParche(Parche parche, UUID userId) {
        if (parche.getStatus() != ParcheStatus.ACTIVE) {
            log.debug("Parche {} no está activo ({}), se omite", parche.getId(), parche.getStatus());
            return;
        }

        boolean isOwner = parche.getOwnerId().equals(userId);

        if (!isOwner) {
            memberRepository.deleteByParcheIdAndStudentId(parche.getId(), userId);
            log.debug("Usuario {} eliminado como miembro del parche {}", userId, parche.getId());
            return;
        }

        int memberCount = memberRepository.countByParcheId(parche.getId());

        if (memberCount <= 1) {
            parche.setStatus(ParcheStatus.FILED);
            parcheRepository.save(parche);
            memberRepository.deleteByParcheIdAndStudentId(parche.getId(), userId);
            log.info("Parche {} archivado: el owner {} era el único miembro", parche.getId(), userId);
        } else {
            transferOwnershipAndLeave(parche, userId);
        }
    }

    private void transferOwnershipAndLeave(Parche parche, UUID userId) {
        UUID newOwnerId = memberRepository.findByParcheId(parche.getId())
                .stream()
                .filter(m -> !m.getStudentId().equals(userId))
                .min(Comparator.comparing(Member::getUnionDate))
                .map(Member::getStudentId)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró un miembro alternativo en el parche " + parche.getId()));

        parche.setOwnerId(newOwnerId);
        parcheRepository.save(parche);
        memberRepository.deleteByParcheIdAndStudentId(parche.getId(), userId);

        log.info("Ownership del parche {} transferido de {} a {}", parche.getId(), userId, newOwnerId);
    }
}
