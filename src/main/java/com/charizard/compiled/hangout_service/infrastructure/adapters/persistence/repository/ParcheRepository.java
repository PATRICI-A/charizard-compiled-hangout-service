package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link ParcheEntity}.
 * Proporciona métodos de consulta estándar y personalizados,
 * incluyendo soporte para Specifications (filtros dinámicos).
 */
@Repository
public interface ParcheRepository extends JpaRepository<ParcheEntity, UUID>, JpaSpecificationExecutor<ParcheEntity> {

    List<ParcheEntity> findByStatus(ParcheStatus status);

    List<ParcheEntity> findByOwnerId(UUID ownerId);

    List<ParcheEntity> findByType(ParcheType type);

    List<ParcheEntity> findByTypeAndStatus(ParcheType type, ParcheStatus status);

    @Query("SELECT p FROM ParcheEntity p WHERE p.status = :status " +
           "AND (p.date < :thresholdDate OR (p.date = :thresholdDate AND p.hour < :thresholdTime))")
    List<ParcheEntity> findArchivables(@Param("status") ParcheStatus status,
                                       @Param("thresholdDate") LocalDate thresholdDate,
                                       @Param("thresholdTime") LocalTime thresholdTime);
}
