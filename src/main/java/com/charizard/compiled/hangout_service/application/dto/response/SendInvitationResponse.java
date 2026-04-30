package com.charizard.compiled.hangout_service.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SendInvitationResponse {
    @Schema(description = "List of successfully created invitations")
    private List<InvitationResponse> createdInvitations;
    @Schema(description = "List of errors for invitations that could not be created")
    private List<ErrorResponse> errors;
}
