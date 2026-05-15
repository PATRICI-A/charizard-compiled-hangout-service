package com.charizard.compiled.hangout_service.domain.model;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Entidad de dominio que representa un parche (encuentro social o académico).
 * Un parche puede ser público ({@link ParcheType#PUBLIC}) o privado ({@link ParcheType#PRIVATE}),
 * tiene un cupo máximo de participantes y un ciclo de vida ACTIVE → FILED.
 */
public class Parche {

    /** Identificador único del parche */
    private UUID id;
    /** Nombre descriptivo del parche */
    private String name;
    /** Descripción opcional del encuentro */
    private String description;
    /** Lugar donde se realizará el encuentro */
    private String place;
    /** Categoría temática (música, programación, deportes, etc.) */
    private String category;
    /** Tipo de acceso: público (libre) o privado (por invitación) */
    private ParcheType type;
    /** Fecha de realización del parche */
    private LocalDate date;
    /** Hora de inicio del parche */
    private LocalTime hour;
    /** Cupo máximo de participantes (2-30) */
    private int maximumQuota;
    /** Estado actual: activo o archivado */
    private ParcheStatus status = ParcheStatus.ACTIVE;
    /** ID del estudiante que creó el parche (capitán) */
    private UUID captainId;
    /** Fecha y hora de creación del parche */
    private LocalDateTime creationDate;
    /** Lista de miembros que pertenecen al parche */
    private List<Member> members;
    /** ID opcional de evento externo asociado */
    private UUID eventId;
}
