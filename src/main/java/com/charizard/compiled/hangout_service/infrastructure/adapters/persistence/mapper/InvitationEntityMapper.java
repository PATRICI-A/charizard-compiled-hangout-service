package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper;

import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import org.mapstruct.Mapper;

/**
 * Mapper between the domain model Invitation and the JPA entity InvitationEntity.
 * Uses MapStruct for automatic code generation.
 */
@Mapper(componentModel = "spring")
public interface InvitationEntityMapper {

    Invitation toDomain(InvitationEntity entity);

    InvitationEntity toEntity(Invitation domain);
}
