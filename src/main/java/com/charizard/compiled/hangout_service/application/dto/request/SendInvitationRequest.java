package com.charizard.compiled.hangout_service.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SendInvitationRequest {
    @Schema(description = "List of student IDs to invite", example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"660e8400-e29b-41d4-a716-446655440001\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Size(max = 10)
    private List<UUID> studentIds;
}
