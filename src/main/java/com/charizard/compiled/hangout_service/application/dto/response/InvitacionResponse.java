package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InvitacionResponse {
    @Schema(description = "ID único de la invitación", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "ID del parche", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID parcheId;
    @Schema(description = "ID del estudiante invitado", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID estudianteInvitadoId;
    @Schema(description = "Estado de la invitación", example = "PENDIENTE")
    private EstadoInvitacion estado;
    @Schema(description = "Fecha y hora de envío de la invitación")
    private LocalDateTime fechaEnvio;
}
