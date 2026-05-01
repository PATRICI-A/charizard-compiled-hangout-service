package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
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
public class MemberResponse {
    private UUID id;
    private UUID parcheId;
    private UUID studentId;
    private MemberRole memberRole;
    private LocalDateTime unionDate;
}
