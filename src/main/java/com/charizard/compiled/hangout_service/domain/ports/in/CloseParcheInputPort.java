package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

public interface CloseParcheInputPort {
    void closeParche(UUID id, UUID captainId);
}
