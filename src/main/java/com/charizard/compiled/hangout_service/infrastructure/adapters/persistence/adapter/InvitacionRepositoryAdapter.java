package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Invitacion;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitacionRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitacionEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.InvitacionEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de salida InvitacionRepositoryPort.
 * Convierte entre modelos de dominio y entidades JPA usando el mapper.
 */
@Component
@RequiredArgsConstructor
public class InvitacionRepositoryAdapter implements InvitacionRepositoryPort {

    /**
     * Repositorio JPA para operaciones de persistencia.
     */
    private final InvitacionRepository invitacionRepository;

    /**
     * Mapper para conversión entre dominio y entidad JPA.
     */
    private final InvitacionEntityMapper mapper;

    /**
     * Guarda una invitación convirtiendo del modelo de dominio a la entidad JPA.
     *
     * @param invitacion el modelo de dominio a guardar
     * @return el modelo de dominio guardado con ID generado
     */
    @Override
    public Invitacion save(Invitacion invitacion) {
        InvitacionEntity entity = mapper.toEntity(invitacion);
        InvitacionEntity saved = invitacionRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * Busca una invitación por parche y estudiante invitado.
     *
     * @param parcheId ID del parche
     * @param estudianteId ID del estudiante invitado
     * @return la invitación si existe
     */
    @Override
    public Optional<Invitacion> findByParcheIdAndEstudianteInvitadoId(UUID parcheId, UUID estudianteId) {
        return invitacionRepository.findByParcheIdAndEstudianteInvitadoId(parcheId, estudianteId)
                .map(mapper::toDomain);
    }

    /**
     * Busca invitaciones por estudiante invitado y estado.
     *
     * @param estudianteId ID del estudiante invitado
     * @param estado estado de la invitación
     * @return lista de invitaciones que cumplen los criterios
     */
    @Override
    public List<Invitacion> findByEstudianteInvitadoIdAndEstado(UUID estudianteId, EstadoInvitacion estado) {
        return invitacionRepository.findByEstudianteInvitadoIdAndEstado(estudianteId, estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Busca todas las invitaciones de un parche.
     *
     * @param parcheId ID del parche
     * @return lista de invitaciones del parche
     */
    @Override
    public List<Invitacion> findByParcheId(UUID parcheId) {
        return invitacionRepository.findByParcheId(parcheId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Invitacion> findById(UUID invitacionId) {
        return invitacionRepository.findById(invitacionId).map(mapper::toDomain);
    }
}
