package com.charizard.compiled.hangout_service.application.dto.request;

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
/**
 * DTO de solicitud para crear un nuevo parche.
 * Contiene todos los campos necesarios con sus validaciones.
 */
public class CreateParcheRequest {

    /** Nombre del parche (obligatorio, no vacío) */
    @NotBlank(message = "Name cannot be blank")
    @Schema(example = "Parche de estudio")
    private String name;

    /** Descripción opcional del parche */
    @Schema(example = "Repaso grupal de matemáticas")
    private String description;

    /** Lugar de encuentro (obligatorio, no vacío) */
    @NotBlank(message = "Place cannot be blank")
    @Schema(example = "Café del edificio Bernardo")
    private String place;

    /** Categoría temática del parche (obligatorio) */
    @NotBlank(message = "Category is required")
    @Schema(example = "MUSIC")
    private String category;

    /** Fecha de realización (obligatorio, debe ser hoy o futura) */
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be today or in the future")
    @Schema(example = "2026-05-15", description = "Date in ISO format (yyyy-MM-dd)")
    private LocalDate date;

    /** Hora de inicio (obligatorio) */
    @NotNull(message = "Hour is required")
    @Schema(example = "14:00:00", description = "Hour in ISO format (HH:mm:ss)")
    private LocalTime hour;

    /** Cupo máximo de participantes (1-30) */
    @Min(value = 1, message = "Maximum quota must be at least 1")
    @Max(value = 30, message = "Maximum quota cannot exceed 30")
    @Schema(example = "10")
    private int maximumQuota;

    /** Tipo de acceso: PUBLIC o PRIVATE (obligatorio) */
    @NotNull(message = "Type is required")
    @Schema(example = "PUBLIC", allowableValues = {"PUBLIC", "PRIVATE"})
    private ParcheType type;

    /** ID opcional de evento externo asociado */
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID eventId;

    /** URL de imagen del parche (opcional, el front la sube al storage y envía la URL) */
    @Schema(example = "https://storage.example.com/parches/imagen.jpg")
    private String imageUrl;

}
