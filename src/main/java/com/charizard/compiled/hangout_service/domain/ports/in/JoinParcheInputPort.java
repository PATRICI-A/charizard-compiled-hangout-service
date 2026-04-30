package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;

import java.util.UUID;

public interface JoinParcheInputPort {
    MemberResponse unirseAParche(UUID parcheId, UUID studentId);
}
