package com.charizard.compiled.hangout_service.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EnviarInvitacionResponse {
    @Schema(description = "Lista de invitaciones creadas exitosamente")
    private List<InvitacionResponse> invitacionesCreadas;
    @Schema(description = "Lista de errores para invitaciones que no se pudieron crear")
    private List<ErrorResponse> errores;
}
