package com.charizard.compiled.hangout_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Entidad de dominio que representa la membresía de un estudiante en un parche.
 * El ownership se almacena en el campo ownerId del Parche, no aquí.
 */
public class Member {

    /** Identificador único de la membresía */
    private UUID id;
    /** ID del parche al que pertenece el miembro */
    private UUID parcheId;
    /** ID del estudiante miembro */
    private UUID studentId;
    /** Fecha y hora en que el estudiante se unió al parche */
    private LocalDateTime unionDate;

}
