package com.charizard.compiled.hangout_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de respuesta con los datos de un lugar (Place) obtenido del microservicio de Places.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    private UUID id;
    private String name;
    private String address;
}
