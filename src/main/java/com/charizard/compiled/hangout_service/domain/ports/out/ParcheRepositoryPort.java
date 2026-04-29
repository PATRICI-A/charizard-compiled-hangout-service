package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import java.util.Optional;
import java.util.UUID;

public interface ParcheRepositoryPort {
    Optional<ParcheEntity> findById(UUID id);
}
