package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface InvitacionRepository extends JpaRepository<InvitacionEntity, UUID> {

    Optional<InvitacionEntity> findByParcheIdAndEstudianteInvitadoId(UUID parcheId, UUID estudianteId);

    List<InvitacionEntity> findByEstudianteInvitadoIdAndEstado(UUID estudianteId, EstadoInvitacion estado);

    List<InvitacionEntity> findByParcheId(UUID parcheId);
}
