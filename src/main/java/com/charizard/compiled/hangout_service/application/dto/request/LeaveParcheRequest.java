package com.charizard.compiled.hangout_service.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de solicitud para salir de un parche.
 * Si el caller es el owner, debe indicar a quién transferir el ownership
 * antes de salir. Si no es el owner, el campo es ignorado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveParcheRequest {

    /**
     * ID del miembro al que se le transfiere el ownership.
     * Obligatorio si el caller es el owner del parche.
     */
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000",
            description = "Required when the caller is the owner. The new owner must already be a member.")
    private UUID newOwnerId;
}
