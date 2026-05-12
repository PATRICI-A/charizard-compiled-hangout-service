package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de MapStruct para transformar entre la entidad JPA {@link MemberEntity}
 * y la entidad de dominio {@link Member}.
 */
@Mapper(componentModel = "spring")
public interface MemberEntityMapper {

    /** Convierte una entidad JPA a entidad de dominio */
    Member toDomain(MemberEntity entity);

    /** Convierte una entidad de dominio a entidad JPA */
    MemberEntity toEntity(Member domain);
}
