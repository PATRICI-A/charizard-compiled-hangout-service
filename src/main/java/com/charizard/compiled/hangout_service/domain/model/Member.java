package com.charizard.compiled.hangout_service.domain.model;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    private UUID id;
    private UUID parcheId;
    private UUID studentId;
    private LocalDateTime unionDate;
    private MemberRole memberRole;

}
