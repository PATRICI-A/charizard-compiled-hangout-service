package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;

import java.util.List;
import java.util.UUID;

public interface GetParcheInputPort {
    List<ParcheResponse> getParches(ParcheType tipo, ParcheStatus estado);
    ParcheResponse getParcheById(UUID id);
}
