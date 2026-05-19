package com.charizard.compiled.hangout_service.application.mapper;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * Mapper de MapStruct para transformar entre la entidad de dominio {@link Parche}
 * y los DTOs de la capa de aplicación.
 */
@Mapper(componentModel = "spring", imports = {ParcheStatus.class})
public interface ParcheMapper {

    /**
     * Convierte un {@link Parche} de dominio a {@link ParcheResponse} para la API.
     *
     * @param parche      entidad de dominio del parche
     * @param memberCount número actual de miembros
     * @return DTO de respuesta
     */
    @Mapping(source = "memberCount", target = "actualMembers")
    ParcheResponse toResponse(Parche parche, int memberCount);

    /**
     * Convierte un {@link CreateParcheRequest} a un {@link Parche} de dominio.
     * Asigna el owner y establece el estado inicial como ACTIVE.
     *
     * @param request  datos de solicitud
     * @param ownerId  ID del estudiante que crea el parche
     * @return entidad de dominio del parche
     */
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(ParcheStatus.ACTIVE)")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "members", ignore = true)
    Parche toDomain(CreateParcheRequest request, UUID ownerId);
}
