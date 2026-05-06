package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheCategory;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateParcheRequest {

    @NotBlank(message = "Name cannot be blank")
    @Schema(example = "Parche de estudio")
    private String name;

    @Schema(example = "Repaso grupal de matemáticas")
    private String description;

    @NotBlank(message = "Place cannot be blank")
    @Schema(example = "Café del edificio Bernardo")
    private String place;

    @NotNull(message = "Category is required")
    @Schema(example = "MUSIC", allowableValues = {"MUSIC", "ART", "DANCE"})
    private ParcheCategory category;

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be today or in the future")
    @Schema(example = "2026-05-15", description = "Date in ISO format (yyyy-MM-dd)")
    private LocalDate date;

    @NotNull(message = "Hour is required")
    @Schema(example = "14:00:00", description = "Hour in ISO format (HH:mm:ss)")
    private LocalTime hour;

    @Min(value = 2, message = "Maximum quota must be at least 2")
    @Max(value = 30, message = "Maximum quota cannot exceed 30")
    @Schema(example = "10")
    private int maximumQuota;

    @NotNull(message = "Type is required")
    @Schema(example = "PUBLIC", allowableValues = {"PUBLIC", "PRIVATE"})
    private ParcheType type;

    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID eventId;

}
