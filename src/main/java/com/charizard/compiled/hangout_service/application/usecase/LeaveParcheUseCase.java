package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.LeaveParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveParcheUseCase implements LeaveParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;

    @Override
    public void salirDeParche(UUID parcheId, UUID studentId) {
        var parche = parcheRepository.findById(parcheId)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + parcheId));

        if (!memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student is not a member of this parche");
        }

        if (parche.getStatus() == ParcheStatus.FILED) {
            throw new IllegalArgumentException("Cannot leave an archived parche");
        }

        if (parche.getCaptainId().equals(studentId)) {
            throw new IllegalArgumentException("Captain cannot leave without transferring leadership first");
        }

        memberRepository.deleteByParcheIdAndStudentId(parcheId, studentId);
    }
}
