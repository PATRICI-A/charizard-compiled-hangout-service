package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;

import java.util.UUID;

public interface UpdateParcheInputPort {
    ParcheResponse updateParche(UUID id, UpdateParcheRequest req, UUID solicitanteId);
}
