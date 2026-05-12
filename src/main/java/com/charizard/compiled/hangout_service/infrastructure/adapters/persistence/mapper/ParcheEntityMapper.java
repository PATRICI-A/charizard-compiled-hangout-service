package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de MapStruct para transformar entre la entidad JPA {@link ParcheEntity}
 * y la entidad de dominio {@link Parche}.
 */
@Mapper(componentModel = "spring")
public interface ParcheEntityMapper {

    /** Convierte una entidad JPA a entidad de dominio (ignora la lista de miembros) */
    @Mapping(target = "members", ignore = true)
    Parche toDomain(ParcheEntity entity);

    /** Convierte una entidad de dominio a entidad JPA */
    ParcheEntity toEntity(Parche parche);
}
