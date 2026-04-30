package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InvitationResponse {
    @Schema(description = "Unique invitation ID", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "Parche ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID parcheId;
    @Schema(description = "Invited student ID", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID invitedStudentId;
    @Schema(description = "Invitation status", example = "PENDING")
    private InvitationStatus status;
    @Schema(description = "Date and time the invitation was sent")
    private LocalDateTime sentAt;
    @Schema(description = "Date and time the invitation was responded to")
    private LocalDateTime respondedAt;
}
