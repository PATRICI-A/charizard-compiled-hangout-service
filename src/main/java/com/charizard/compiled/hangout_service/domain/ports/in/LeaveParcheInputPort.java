package com.charizard.compiled.hangout_service.domain.ports.in;

import java.util.UUID;

public interface LeaveParcheInputPort {
    void salirDeParche(UUID parcheId, UUID studentId);
}
