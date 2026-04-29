package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParcheRepository extends JpaRepository<ParcheEntity, UUID> {

    List<ParcheEntity> findByStatus(ParcheStatus status);

    List<ParcheEntity> findByCaptainId(UUID captainId);

    List<ParcheEntity> findByType(ParcheType type);

    List<ParcheEntity> findByTypeAndStatus(ParcheType type, ParcheStatus status);
}
