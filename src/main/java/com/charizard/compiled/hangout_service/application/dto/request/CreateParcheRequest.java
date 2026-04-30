package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
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
    private String name;

    private String description;

    @NotBlank(message = "Place cannot be blank")
    private String place;

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be today or in the future")
    private LocalDate date;

    @NotNull(message = "Hour is required")
    private LocalTime hour;

    @Min(value = 2, message = "Maximum quota must be at least 2")
    @Max(value = 30, message = "Maximum quota cannot exceed 30")
    private int maximumQuota;

    @NotNull(message = "Type is required")
    private ParcheType type;

    private UUID eventId;

}
