package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de una invitación a un parche privado.
 * Incluye el estado actual y las fechas de envío/respuesta.
 */
@Data
@Builder
public class InvitationResponse {
    /** ID único de la invitación */
    @Schema(description = "Unique invitation ID", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    /** ID del parche al que se invita */
    @Schema(description = "Parche ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID parcheId;
    /** ID del estudiante invitado */
    @Schema(description = "Invited student ID", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID invitedStudentId;
    /** Estado de la invitación (PENDING / ACCEPTED / REJECTED) */
    @Schema(description = "Invitation status", example = "PENDING")
    private InvitationStatus status;
    /** Fecha y hora de envío */
    @Schema(description = "Date and time the invitation was sent")
    private LocalDateTime sentAt;
    /** Fecha y hora de respuesta (null si pendiente) */
    @Schema(description = "Date and time the invitation was responded to")
    private LocalDateTime respondedAt;
}
