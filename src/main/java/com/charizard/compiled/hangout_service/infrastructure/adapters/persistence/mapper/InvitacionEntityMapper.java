package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper;

import com.charizard.compiled.hangout_service.domain.model.Invitacion;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitacionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper entre la entidad de dominio Invitacion y la entidad JPA InvitacionEntity.
 * Utiliza MapStruct para generación automática de código de conversión.
 */
@Mapper(componentModel = "spring")
public interface InvitacionEntityMapper {

    /**
     * Convierte una InvitacionEntity (JPA) a Invitacion (dominio).
     *
     * @param entity la entidad JPA
     * @return el modelo de dominio
     */
    Invitacion toDomain(InvitacionEntity entity);

    /**
     * Convierte una Invitacion (dominio) a InvitacionEntity (JPA).
     *
     * @param domain el modelo de dominio
     * @return la entidad JPA
     */
    InvitacionEntity toEntity(Invitacion domain);
}
