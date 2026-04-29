package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcheResponse {

    private UUID id;
    private String name;
    private ParcheType type;
    private int maximumQuota;
    private int actualMembers;
    private UUID captainId;
    private LocalDateTime dateRealization;

}
