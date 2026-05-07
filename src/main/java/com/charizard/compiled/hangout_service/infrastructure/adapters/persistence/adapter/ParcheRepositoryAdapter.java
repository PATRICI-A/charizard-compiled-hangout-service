package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.ParcheEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    public List<Parche> findByFilters(ParcheType type, ParcheStatus status, String nombre, LocalDate fecha) {
        Specification<ParcheEntity> spec = Specification.where((Specification<ParcheEntity>) null);

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + nombre.toLowerCase().trim() + "%"));
        }
        if (fecha != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("date"), fecha));
        }

        return parcheRepository.findAll(spec).stream().map(mapper::toDomain).toList();
    }
}
