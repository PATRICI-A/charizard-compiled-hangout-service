package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
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
 * DTO de respuesta con los datos completos de un parche.
 * Incluye información del parche y el conteo actual de miembros.
 */
public class ParcheResponse {

    /** Identificador único del parche */
    private UUID id;
    /** Nombre del parche */
    private String name;
    /** Descripción del parche */
    private String description;
    /** Lugar de encuentro */
    private String place;
    /** Categoría temática */
    private String category;
    /** Tipo de acceso (PUBLIC / PRIVATE) */
    private ParcheType type;
    /** Estado actual (ACTIVE / FILED) */
    private ParcheStatus status;
    /** Cupo máximo de participantes */
    private int maximumQuota;
    /** Número actual de miembros inscritos */
    private int actualMembers;
    /** ID del estudiante dueño del parche */
    private UUID ownerId;
    /** Fecha de realización */
    private LocalDate date;
    /** Hora de inicio */
    private LocalTime hour;
    /** URL de la imagen del parche (puede ser null) */
    private String imageUrl;
}
