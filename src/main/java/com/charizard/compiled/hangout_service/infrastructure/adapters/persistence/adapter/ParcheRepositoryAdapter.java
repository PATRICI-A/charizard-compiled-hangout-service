package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.ParcheEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public Parche save(Parche parche) {
        return mapper.toDomain(parcheRepository.save(mapper.toEntity(parche)));
    }

    @Override
    public List<Parche> findArchivables(ParcheStatus status, LocalDateTime threshold) {
        return parcheRepository.findArchivables(status, threshold).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Parche> findByFilters(ParcheType type, ParcheStatus status) {
        if (type != null && status != null) {
            return parcheRepository.findByTypeAndStatus(type, status).stream().map(mapper::toDomain).toList();
        } else if (type != null) {
            return parcheRepository.findByType(type).stream().map(mapper::toDomain).toList();
        } else if (status != null) {
            return parcheRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
        }
        return parcheRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
