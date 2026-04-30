package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
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

    private String name;

    private String description;

    private String place;

    private LocalDate date;

    private LocalTime hour;

    @Min(value = 2, message = "Maximum quota must be at least 2")
    @Max(value = 50, message = "Maximum quota cannot exceed 50")
    private Integer maximumQuota;

    private ParcheType type;

    private UUID eventId;

}
