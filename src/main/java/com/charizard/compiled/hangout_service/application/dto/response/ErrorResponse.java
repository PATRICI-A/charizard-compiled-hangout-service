package com.charizard.compiled.hangout_service.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * DTO de respuesta para errores individuales al procesar invitaciones masivas.
 * Contiene el ID del estudiante, el mensaje de error y el código HTTP.
 */
@Data
@Builder
public class ErrorResponse {
    /** ID del estudiante relacionado con el error */
    @Schema(description = "Student ID related to the error", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID studentId;
    /** Mensaje descriptivo del error */
    @Schema(description = "Descripción del error", example = "El estudiante ya es miembro de este parche")
    private String error;
    /** Código de estado HTTP del error */
    @Schema(description = "Código de estado HTTP", example = "409")
    private int status;
}
