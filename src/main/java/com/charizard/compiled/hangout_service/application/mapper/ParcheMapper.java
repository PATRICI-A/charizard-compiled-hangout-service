package com.charizard.compiled.hangout_service.application.mapper;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {ParcheStatus.class})
public interface ParcheMapper {

    @Mapping(source = "memberCount", target = "actualMembers")
    ParcheResponse toResponse(Parche parche, int memberCount);

    @Mapping(source = "captainId", target = "captainId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(ParcheStatus.ACTIVE)")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "members", ignore = true)
    Parche toDomain(CreateParcheRequest request, UUID captainId);
}
