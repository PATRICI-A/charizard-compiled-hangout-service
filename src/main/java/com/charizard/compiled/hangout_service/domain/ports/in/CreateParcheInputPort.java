package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;

import java.util.UUID;

public interface CreateParcheInputPort {
    ParcheResponse createParche(CreateParcheRequest request, UUID captainId);
}
