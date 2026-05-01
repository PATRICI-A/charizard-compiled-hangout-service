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
public class UpdateParcheRequest {

    @Schema(example = "Parche de estudio actualizado")
    private String name;

    @Schema(example = "Descripción actualizada")
    private String description;

    @Schema(example = "Café del edificio Bernardo")
    private String place;

    @Schema(example = "2026-05-20", description = "Date in ISO format (yyyy-MM-dd)")
    private LocalDate date;

    @Schema(example = "16:00", description = "Hour in ISO format (HH:mm)")
    private LocalTime hour;

    @Min(value = 2, message = "Maximum quota must be at least 2")
    @Max(value = 50, message = "Maximum quota cannot exceed 50")
    @Schema(example = "15")
    private Integer maximumQuota;

    @Schema(example = "PRIVATE", allowableValues = {"PUBLIC", "PRIVATE"})
    private ParcheType type;

    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID eventId;

}
