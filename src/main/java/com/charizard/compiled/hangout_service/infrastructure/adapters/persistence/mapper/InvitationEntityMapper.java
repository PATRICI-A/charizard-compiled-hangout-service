package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper;

import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import org.mapstruct.Mapper;

/**
 * Mapper de MapStruct para transformar entre la entidad JPA {@link InvitationEntity}
 * y la entidad de dominio {@link Invitation}.
 */
@Mapper(componentModel = "spring")
public interface InvitationEntityMapper {

    Invitation toDomain(InvitationEntity entity);

    InvitationEntity toEntity(Invitation domain);
}
