package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de solicitud para actualizar parcialmente un parche existente.
 * Todos los campos son opcionales; solo se actualizan los enviados.
 */
public class UpdateParcheRequest {

    /** Nueva descripción (opcional) */
    @Schema(example = "Descripción actualizada")
    private String description;

    /** Nuevo lugar de encuentro (opcional) */
    @Schema(example = "Café del edificio Bernardo")
    private String place;

    /** Nueva categoría (opcional) */
    @Schema(example = "MUSIC")
    private String category;

    /** Nueva fecha (opcional, formato yyyy-MM-dd) */
    @Schema(example = "2026-05-20", description = "Date in ISO format (yyyy-MM-dd)")
    private LocalDate date;

    /** Nueva hora (opcional, formato HH:mm) */
    @Schema(example = "16:00", description = "Hour in ISO format (HH:mm)")
    private LocalTime hour;

    /** Nuevo cupo máximo (1-30, opcional) */
    @Min(value = 1, message = "Maximum quota must be at least 1")
    @Max(value = 30, message = "Maximum quota cannot exceed 30")
    @Schema(example = "15")
    private Integer maximumQuota;

    /** Nuevo tipo de acceso (opcional) */
    @Schema(example = "PRIVATE", allowableValues = {"PUBLIC", "PRIVATE"})
    private ParcheType type;

    /** Nuevo ID de evento externo (opcional) */
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID eventId;

    /** Nueva URL de imagen (opcional) */
    @Schema(example = "https://storage.example.com/parches/imagen.jpg")
    private String imageUrl;

}
