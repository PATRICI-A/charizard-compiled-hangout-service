package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.CloseParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso para archivar manualmente un parche (soft delete).
 * Cambia el estado del parche de ACTIVE a FILED.
 * Solo ROLE_ADMIN puede ejecutar esta acción (verificación en el controlador).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CloseParcheUseCase implements CloseParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;

    @Override
    public void closeParche(UUID id) {
        Parche parche = parcheRepository.findById(id)
                .orElseThrow(() -> new ParcheNotFoundException("Parche not found with id: " + id));

        parche.setStatus(ParcheStatus.FILED);
        parcheRepository.save(parche);
    }
}
