package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.CloseParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CloseParcheUseCase implements CloseParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;

    @Override
    public void closeParche(UUID id, UUID captainId) {
        Parche parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));

        if (!parche.getCaptainId().equals(captainId)) {
            throw new AccessDeniedException("Only the captain can delete this parche");
        }

        parche.setStatus(ParcheStatus.FILED);
        parcheRepository.save(parche);
    }
}
