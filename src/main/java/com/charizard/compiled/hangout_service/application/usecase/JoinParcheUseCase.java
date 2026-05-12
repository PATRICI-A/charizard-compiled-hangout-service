package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaximumCapacityReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.JoinParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinParcheUseCase implements JoinParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ParcheEventPublisherPort parcheEventPublisher;

    @Override
    public MemberResponse unirseAParche(UUID parcheId, UUID studentId) {
        Parche parche = parcheRepository.findById(parcheId)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + parcheId));

        if (parche.getStatus() == ParcheStatus.FILED) {
            throw new IllegalArgumentException("Cannot join an archived parche");
        }

        if (memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)) {
            throw new StudentAlreadyMemberException("Student is already a member of this parche");
        }

        if (memberRepository.countByParcheId(parcheId) >= parche.getMaximumQuota()) {
            throw new MaximumCapacityReachedException("Parche is already full");
        }

        if (memberRepository.countParchesActivosByStudentId(studentId) >= 5) {
            throw new MaxHangoutsReachedException();
        }

        Member saved = memberRepository.save(Member.builder()
                .parcheId(parcheId)
                .studentId(studentId)
                .memberRole(MemberRole.STUDENT)
                .build());

        parcheEventPublisher.publishMemberJoined(
                parcheId, parche.getName(), parche.getCaptainId(), studentId);

        return MemberResponse.builder()
                .id(saved.getId())
                .parcheId(saved.getParcheId())
                .studentId(saved.getStudentId())
                .memberRole(saved.getMemberRole())
                .unionDate(saved.getUnionDate())
                .build();
    }
}
