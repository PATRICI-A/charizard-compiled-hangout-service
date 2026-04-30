package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.ParcheEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ParcheRepositoryAdapter implements ParcheRepositoryPort {

    private final ParcheRepository parcheRepository;
    private final ParcheEntityMapper mapper;

    @Override
    public Optional<Parche> findById(UUID id) {
        return parcheRepository.findById(id).map(mapper::toDomain);
    }
}
