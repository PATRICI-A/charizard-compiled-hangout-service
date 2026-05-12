package com.charizard.compiled.hangout_service.application.dto.response;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de respuesta con los datos de la membresía de un estudiante en un parche.
 */
public class MemberResponse {
    /** Identificador único de la membresía */
    private UUID id;
    /** ID del parche al que pertenece */
    private UUID parcheId;
    /** ID del estudiante miembro */
    private UUID studentId;
    /** Rol del miembro (CAPTAIN / STUDENT) */
    private MemberRole memberRole;
    /** Fecha y hora de ingreso al parche */
    private LocalDateTime unionDate;
}
