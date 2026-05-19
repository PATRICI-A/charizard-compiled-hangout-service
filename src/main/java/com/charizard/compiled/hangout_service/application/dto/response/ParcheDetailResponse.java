package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta detallada de un parche (GET /{id}).
 * Incluye la lista de miembros, el evento asociado y el lugar,
 * enriquecidos con datos de microservicios externos (OpenFeign).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcheDetailResponse {

    private UUID id;
    private String name;
    private String description;
    private String category;
    private ParcheType type;
    private ParcheStatus status;
    private int maximumQuota;
    private int actualMembers;
    private UUID ownerId;
    private LocalDate date;
    private LocalTime hour;
    private String imageUrl;

    /** Datos del lugar (puede ser null si el microservicio no está disponible) */
    private PlaceResponse place;

    /** Datos del evento asociado (puede ser null si no tiene eventId o el servicio no está disponible) */
    private EventResponse event;

    /** Lista de miembros del parche */
    private List<MemberResponse> members;
}
