package com.charizard.compiled.hangout_service.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO de respuesta para el envío masivo de invitaciones.
 * Separa las invitaciones creadas exitosamente de los errores individuales.
 */
@Data
@Builder
public class SendInvitationResponse {
    /** Lista de invitaciones creadas exitosamente */
    @Schema(description = "List of successfully created invitations")
    private List<InvitationResponse> createdInvitations;
    /** Lista de errores para invitaciones que no pudieron crearse */
    @Schema(description = "List of errors for invitations that could not be created")
    private List<ErrorResponse> errors;
}
