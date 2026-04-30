package com.charizard.compiled.hangout_service.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ErrorResponse {
    @Schema(description = "Student ID related to the error", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID studentId;
    @Schema(description = "Descripción del error", example = "El estudiante ya es miembro de este parche")
    private String error;
    @Schema(description = "Código de estado HTTP", example = "409")
    private int status;
}
