package com.charizard.compiled.hangout_service.infrastructure.adapters.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationSentMessage {

    private String invitationId;
    private String parcheId;
    private String invitedStudentId;
    private String captainId;
    private LocalDateTime occurredAt;
}
