package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
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

    @NotNull(message = "The name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Parche's place cannot be blank")
    private String place;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime hour;

    @NotNull
    private int maximumQuota;

    @NotNull(message = "Parche's type cannot be blank")
    private ParcheType type;

    private UUID eventId;

}
