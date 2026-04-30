package com.charizard.compiled.hangout_service.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record NuevoMiembroEvent(
        UUID capitanId,
        UUID estudianteId,
        String nombreParche,
        LocalDateTime timestamp
) {}
