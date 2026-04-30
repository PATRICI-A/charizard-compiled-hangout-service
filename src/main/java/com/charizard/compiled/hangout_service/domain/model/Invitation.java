package com.charizard.compiled.hangout_service.domain.model;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing an invitation to a parche.
 * This class is infrastructure-independent (no JPA annotations).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    private UUID id;
    private UUID parcheId;
    private UUID captainId;
    private UUID invitedStudentId;
    private InvitationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
}
