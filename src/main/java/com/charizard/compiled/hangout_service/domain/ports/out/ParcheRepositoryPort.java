package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Parche;

import java.util.Optional;
import java.util.UUID;

public interface ParcheRepositoryPort {
    Optional<Parche> findById(UUID id);
}
