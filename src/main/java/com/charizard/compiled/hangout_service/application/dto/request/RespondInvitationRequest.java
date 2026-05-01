package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RespondInvitationRequest {

    @NotNull
    @Schema(example = "ACCEPTED", allowableValues = {"ACCEPTED", "REJECTED"})
    private InvitationStatus answer;
}
