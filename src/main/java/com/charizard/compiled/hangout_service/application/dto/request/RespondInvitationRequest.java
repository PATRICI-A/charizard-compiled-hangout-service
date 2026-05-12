package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de solicitud para responder una invitación pendiente.
 * El estudiante invitado puede aceptar (ACCEPTED) o rechazar (REJECTED).
 */
@Data
public class RespondInvitationRequest {

    /** Respuesta a la invitación: ACCEPTED o REJECTED */
    @NotNull
    @Schema(example = "ACCEPTED", allowableValues = {"ACCEPTED", "REJECTED"})
    private InvitationStatus answer;
}
