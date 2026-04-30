package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberEntityMapper {

    Member toDomain(MemberEntity entity);

    @Mapping(target = "parche", ignore = true)
    MemberEntity toEntity(Member domain);
}
